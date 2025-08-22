$(document).ready(function () {

    loadStationForSelection();

    const staffModal = $("#staffModal");
    const staffUpdateModal = $("#staffUpdateModal");
    const addStaffBtn = $("#addStaffBtn");
    const closeModalBtns = $("#closeModal, #closeUpdateModal, #cancelBtn, #cancelUpdateBtn");

    addStaffBtn.on("click", function () {
        staffModal.addClass("active");
        loadStationForSelection();
        resetRegisterForm();
    });

    closeModalBtns.on("click", function () {
        staffModal.removeClass("active");
        staffUpdateModal.removeClass("active");

    });

    $(".fa-edit").on("click", function () {
        staffUpdateModal.addClass("active");
    });

    $(window).on("click", function (event) {
        if ($(event.target).is(staffModal)) {
        staffModal.removeClass("active");
        }
        if ($(event.target).is(staffUpdateModal)) {
        staffUpdateModal.removeClass("active");
        }
    });

    $("#staffPassword").on("input", function () {
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


    $("#toggleStaffPassword").click(function () {
        const passwordField = $("#staffPassword"); 
        const type = passwordField.attr("type") === "password" ? "text" : "password";
        passwordField.attr("type", type);

        $(this).html(
            type === "password"
            ? '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>'
            : '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" /></svg>'
        );

    });


    function loadStationForSelection() {

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTg3NjIwNSwiZXhwIjoxMDc1NTg3NjIwNX0.BAzkBN9fiQhQr3VLl8s2N3AxS4RhjVsWCkMn7C5wUQw");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code === 200) {
                const allStations = result.data;
                allStations.sort((a, b) => a.name.localeCompare(b.name));
                $("#staffStationSelection").empty();
                $("#staffStationSelection").append('<option disabled selected value="">Select a Station</option>');

                allStations.forEach(station => {
                    if (station.inService) {
                        $("#staffStationSelection").append(`<option value="${station.name}">${station.name} (${station.stationCode})</option>`);
                        return;
                    }
                    $("#staffStationSelection").append(`<option disabled value="">${station.name} (${station.stationCode})  - out-of-service</option>`);
                });
            }
        })
        .catch((error) => console.error(error));



    }

   $("#staffStationSelection").on("change", function () {
    let selectedStationValue = $("#staffStationSelection").val();

    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTg5MTI4MiwiZXhwIjoxMDc1NTg5MTI4Mn0.X3tTkUzvgmxRfRb-_09lmOxM_00OP2uUqssPiGDDnWQ");

    const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
    };

    fetch(`http://localhost:8080/api/v1/raillankapro/counter/get/counternumbers/by/stationname/${selectedStationValue}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result);

            const disabledCounters = result.data; 

            $("#staffCounterSelection").empty();
            $("#staffCounterSelection").append(`<option disabled selected value="">Select a Counter</option>`);

            const counters = ["COUNTER_1", "COUNTER_2", "COUNTER_3"];

            counters.forEach(c => {
                const isDisabled = disabledCounters.includes(c);
                const label = c.replace("_", " "); 

                if (isDisabled) {
                    $("#staffCounterSelection").append(
                        `<option value="${c}" disabled>${label} - already assigned</option>`
                    );
                } else {
                    $("#staffCounterSelection").append(
                        `<option value="${c}">${label}</option>`
                    );
                }
            });
        })
        .catch((error) => console.error(error));
    });

    $("#staffModal").on("submit" , function(e) {
        e.preventDefault();

        const staffFirstname = $("#staffFirstname").val().trim();
        const staffLastname = $("#staffLastname").val().trim();
        const staffNIC = $("#staffNIC").val().trim();
        const staffDOB = $("#staffDOB").val().trim();
        const staffContactNumber = $("#staffContactNumber").val().trim();
        const staffEmail = $("#staffEmail").val().trim();
        const staffAddress = $("#staffAddress").val().trim();
        const staffUsername = $("#staffUsername").val().trim();
        const staffPassword = $("#staffPassword").val().trim();
        const staffYearsOfExperience = $("#staffYearsOfExperience").val().trim();
        const staffStationSelection = $("#staffStationSelection").val();
        const staffCounterSelection = $("#staffCounterSelection").val();

        console.log(staffCounterSelection)

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        const raw = JSON.stringify({
        "firstname": staffFirstname,
        "lastname": staffLastname,
        "userName": staffUsername,
        "password": staffPassword,
        "idNumber": staffNIC,
        "phoneNumber": staffContactNumber,
        "railwayStation": staffStationSelection,
        "counterNumber": staffCounterSelection,
        "dob": staffDOB,
        "email": staffEmail,
        "address": staffAddress,
        "yearsOfExperience": staffYearsOfExperience
        });

        const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/auth/register/counter", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code === 409) {
                $("#staffUsernameMsg").text(result.message).addClass("text-red-500")
                return;
            }

            if (result.code === 201) {
                toastr.success(result.data);
                staffModal.removeClass("active");
            }
        })
        .catch((error) => console.error(error));


    });

    function resetRegisterForm() {
        $("#staffFirstname").val();
        $("#staffLastname").val();
        $("#staffNIC").val();
        $("#staffDOB").val();
        $("#staffContactNumber").val();
        $("#staffEmail").val();
        $("#staffAddress").val();
        $("#staffUsername").val();
        $("#staffPassword").val();
        $("#staffYearsOfExperience").val();
        $("#staffYearsOfExperience").val();
        $("#staffYearsOfExperience").val();

        $("#staffUsernameMsg").text("").removeClass("text-red-500")

    }




});