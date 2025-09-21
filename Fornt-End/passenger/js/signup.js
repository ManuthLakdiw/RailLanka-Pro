$(document).ready(function () {

  const nicRegex = /^(\d{9}[vVxX]|\d{12})$/;
  const passportRegex = /^[A-Z]{1,2}[0-9]{6,9}$/i;
  const nameRegex = /^[A-Za-z\s]{2,50}$/;
  const phoneRegex = /^(?:\+94|0)?7\d{8}$/;
  const usernameRegex = /^[A-Za-z0-9]{6,20}$/;

  let currentStep = 1;
  const totalSteps = 3;
  const stepTitles = [
    "Passenger Type",
    "Personal Information",
    "Account Details",
  ];

   
  $("#termsLink").click(function (e) {
    e.preventDefault();
    $("#termsModal").addClass("active animate__zoomIn").removeClass("animate__zoomOut");
  });

  $("#closeTerms").click(function () {
    $("#termsModal").removeClass("animate__zoomIn").addClass("animate__zoomOut");
      setTimeout(() => {
        $("#termsModal").removeClass("active");
      }, 300);
  });

  $("#termsModal").click(function (e) {
    if (e.target === this) {
        $("#termsModal").removeClass("animate__zoomIn").addClass("animate__zoomOut");
        setTimeout(() => {
          $("#termsModal").removeClass("active");
        }, 300);
    }
  });

  $(".passenger-type").click(function () {
    $(".passenger-type").removeClass("selected");
    $(this).addClass("selected");
    $(this).find('input[type="radio"]').prop("checked", true);
    $("#nextBtn1").prop("disabled", false);

    if ($(this).find("input").val() === "foreign") {
      $("#idLabel").text("Passport Number");
      $("#idNumber").attr("placeholder", "");
      $("#format").text("").addClass("passport")
      $("#phoneContainer").addClass("hidden")
      $("#email").parent().removeClass("md:col-span-1").addClass("md:col-span-2");

    } else {
      $("#idLabel").text("NIC Number");
      $("#idNumber").attr("placeholder", "");
      $("#format").text("Format: 123456789V / 200012345678").removeClass("passport")
      $("#phoneContainer").removeClass("hidden")
      $("#email").parent().removeClass("md:col-span-2").addClass("md:col-span-1");

    }
  });

    

  function updateProgressBar() {
      const progress = (currentStep / totalSteps) * 100;
      $("#progressBarFill").css("width", progress + "%");
      $("#currentStep").text(currentStep);
      $("#stepTitle").text(stepTitles[currentStep - 1]);
  }

  $("#nextBtn1").click(function () {
      if ($('input[name="passengerType"]:checked').length) {
          $("#step1").removeClass("active");
          $("#step2").addClass("active");
          currentStep = 2;
          updateProgressBar();
          }

          resetField();
  });

  $("#nextBtn2").click(function () {
      if (validateStep2()) {
          $("#step2").removeClass("active");
          $("#step3").addClass("active");
          currentStep = 3;
          updateProgressBar();
      }
  });

  $("#backBtn2").click(function () {
      $("#step2").removeClass("active");
      $("#step1").addClass("active");
          currentStep = 1;
          updateProgressBar();
  });

  $("#backBtn3").click(function () {
      $("#step3").removeClass("active");
      $("#step2").addClass("active");
      currentStep = 2;
      updateProgressBar();
  });
        
  $("#step2 input, #step2 select").on("input change", function () {
      $("#nextBtn2").prop("disabled", !validateStep2());
  });


  $("#password, #confirmPassword").on("paste", function (e) {
    e.preventDefault();
  });

  $("#password").on("input", function () {
      const password = $(this).val();
      let strength = 0;

            
      if (password.length > 7) strength++;
            
      if (password.match(/[a-z]/)) strength++;
            
      if (password.match(/[A-Z]/)) strength++;
            
      if (password.match(/[0-9]/)) strength++;
            
      if (password.match(/[^a-zA-Z0-9]/)) strength++;

      strength = Math.min(strength, 4);

      $("#passwordStrength").removeClass().addClass("password-strength strength-" + strength);
  });

  $("#confirmPassword").on("input", function () {
      if ($(this).val() !== $("#password").val()) {
          $(this).addClass("border-red-500");
      } else {
          $(this).removeClass("border-red-500");
      }
          validateStep3();
  });


  $("#termsCheckbox").change(function () {
      validateStep3();
  });

  $("#step3 input").on("input", function () {
      validateStep3();
  });

  $("#username").on("input" , function () {
    $("#usernamelabel").text("6-20 characters (letters and numbers only").removeClass("text-red-500")
  })

  function validateStep2() {
    const title = $("#title").val().trim();
    const firstName = $("#firstName").val().trim();
    const lastName = $("#lastName").val().trim();
    const idNumber = $("#idNumber").val().trim();
    const isForeign = $("#foreignPassenger").is(":checked");
    const isPassport = $("#format").hasClass("passport");

    const isFirstNameValid = nameRegex.test(firstName);
    const isLastNameValid = nameRegex.test(lastName);
    let isIdValid = false;

    if (isForeign) {
    
      isIdValid = isPassport ? passportRegex.test(idNumber) : nicRegex.test(idNumber);

      return (
        title &&
        firstName &&
        lastName &&
        isIdValid &&
        isFirstNameValid &&
        isLastNameValid
      );
    } else {
      const isNicValid = nicRegex.test(idNumber);
      return (
        title &&
        firstName &&
        lastName &&
        isNicValid &&
        isFirstNameValid &&
        isLastNameValid
      );
    }
  }



  function validateStep3() {
      const passwordValid = $("#password").val().length >= 8;
      const passwordsMatch = $("#password").val() === $("#confirmPassword").val();
      const termsChecked = $("#termsCheckbox").is(":checked");
      const usernameValid = $("#username").val().length >= 6;

      
      const phone = $("#phone").val().trim();
      const isValidPhone = phoneRegex.test(phone);

      const phoneHidden = $("#phoneContainer").hasClass("hidden");

      if (phoneHidden) {
              $("#submitBtn").prop(
              "disabled",
              !(passwordValid && passwordsMatch && termsChecked && usernameValid)
            );
      }else {
          $("#submitBtn").prop(
              "disabled",
              !(passwordValid && passwordsMatch && termsChecked && usernameValid && isValidPhone)
            );
      }


    
      
  }

  $(".input-field").focus(function () {
          $(this).addClass("input-highlight");
          $(this).next(".floating-label").addClass("text-blue-600");
      }).blur(function () {
              $(this).removeClass("input-highlight");
              $(this).next(".floating-label").removeClass("text-blue-600");
      });

      
     updateProgressBar();



  function resetField(){

      $("#title").val("");
      $("#firstName").val("");
      $("#lastName").val("");
      $("#idNumber").val("");
      $("#username").val("");
      $("#password").val("");
      $("#confirmPassword").val("");
      $("#phone").val("");
      $("#email").val("");
      $("#termsCheckbox").prop("checked", false);

      $("#firstName").removeClass("border-red-500");
      $("#lastName").removeClass("border-red-500");
      $("#idNumber").removeClass("border-red-500");
      $("#username").removeClass("border-red-500");
      $("#confirmPassword").removeClass("border-red-500");
      $("#phone").removeClass("border-red-500");

      $("#nextBtn2").prop("disabled", true);
      $("#nextBtn3").prop("disabled", true);
  }

  $("#registrationForm").submit(function (e) {
    e.preventDefault();
    
    const passengerType = $('input[name="passengerType"]:checked').val().toUpperCase();
    const title =  $("#title").val();
    const firstName = $("#firstName").val();
    const lastName = $("#lastName").val();
    const idNumber = $("#idNumber").val();
    const username = $("#username").val();
    const password = $("#password").val();
    const phone = $("#phone").val();
    const email = $("#email").val();

    
    console.log("Passenger Type:", passengerType);
    console.log("Title:", title);
    console.log("First Name:", firstName);
    console.log("Last Name:", lastName);
    console.log("ID Number:", idNumber);
    console.log("Username:", username);
    console.log("Password:", password);
    console.log("Phone:", phone);
    console.log("Email:", email);


    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
      "title": title,
      "firstName": firstName,
      "lastName": lastName,
      "username": username,
      "password": password,
      "passengerType": passengerType.toUpperCase(),
      "idNumber": idNumber,
      "phoneNumber": phone ? phone : "N/A",
      "email": email
    });

    const requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow"
    };

    fetch("http://localhost:8080/api/v1/raillankapro/auth/register/passenger", requestOptions)
      .then((response) => response.json())
      .then((result) => {
          console.log(result);
          if (result.code === 409) {
              $("#usernamelabel")
                  .text(result.message)
                  .addClass("text-red-500"); 

                 

          }

          if (result.code === 201) {

              toastr.success(result.data);

              setTimeout(function () {
                resetRegistrationForm();
              },800)
        
              setTimeout(function () {
                 window.location.href = "/animation.html";
              },810)
          }

          if (result.code === 429) {
             const notyf = new Notyf();
                  notyf.error(result.message);
          }


      }).catch((error) => console.error(error));

  });

  function resetRegistrationForm() {
    $("#step3").removeClass("active");
      $("#step1").addClass("active");
      currentStep = 1;
      updateProgressBar();
      $(".passenger-type").removeClass("selected");
      $("#nextBtn1").prop("disabled", true);
      $("#idLabel").text("NIC Number");
      $("#passwordStrength").removeClass().addClass("password-strength strength-0");
      $("#usernamelabel").text("6-20 characters (letters and numbers only").removeClass("text-red-500")

  }

  
});
