$(document).ready(function () {
let currentStep = 1;
const totalSteps = 4;
const stepTitles = [
    "Verify Email",
    "Enter OTP",
    "Reset Password",
    "Success",
];

// Update progress bar
function updateProgressBar() {
    const progress = (currentStep / totalSteps) * 100;
    $("#progressBarFill").css("width", progress + "%");
    $("#currentStep").text(currentStep);
    $("#stepTitle").text(stepTitles[currentStep - 1]);
}

$(".otp-input").on("input", function () {
    let value = $(this).val();

    value = value.replace(/\D/g, "");

    $(this).val(value);

    if (value.length === 1) {
    $(this).next(".otp-input").focus();
    }
});

// Handle email form submission
$("#emailForm").submit(function (e) {
    e.preventDefault();

    $("#otpError, #emailError, #passwordError").addClass("hidden").text("");
    $(".otp-input").val("");

    const email = $("#email").val().trim();

    if (!email || !isValidEmail(email)) {
    $("#emailError")
        .text("Please enter a valid email address")
        .removeClass("hidden");
    return;
    }

    const btn = $(this).find("button[type='submit']");
    const originalText = btn.html();
    btn.html(`
    <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
    </svg>
    Sending...
    `);
    btn.prop("disabled", true);
    

    const requestOptions = {
    method: "POST",
    redirect: "follow"
    };

    setTimeout(() => {
        fetch(`http://localhost:8080/api/v1/raillankapro/auth/reset/password/verify?email=${email}`, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.code === 200) {
                    $("#userEmail").text(email);
                    $("#step1").removeClass("active");
                    $("#step2").addClass("active");
                    currentStep = 2;
                    updateProgressBar();

                    btn.html(originalText);
                    btn.prop("disabled", false);
                }else{
                    const message = result.message;
                    $("#emailError")
                    .text(data)
                    .removeClass("hidden");
                    btn.html(originalText);
                    btn.prop("disabled", false);
                    return; 
                }
            })
            .catch((error) => console.error(error));
    }, 1000);
    
});

// Handle OTP form submission
$("#otpForm").submit(function (e) {
    e.preventDefault();

    $("#passwordError").addClass("hidden").text("");


    // Collect OTP
    let otp = "";
    $(".otp-input").each(function () {
    otp += $(this).val();
    });

    if (otp.length !== 5) {
    $("#otpError")
        .text("Please enter a complete 5-digit code")
        .removeClass("hidden");
    return;
    }

    // Show loading state
    const btn = $(this).find("button[type='submit']");
    const originalText = btn.html();
    btn.html(`
    <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
    </svg>
    Verifying...
    `);
    btn.prop("disabled", true);

    const email = $("#userEmail").text().trim();
    
    const requestOptions = {
        method: "POST",
        redirect: "follow"
    };

    setTimeout(() => {
        console.log(email, otp);
        fetch(`http://localhost:8080/api/v1/raillankapro/auth/reset/password/verify/code?email=${email}&code=${otp}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            const data = result.data;

            if (result.code === 200) {
                if (data) {
                    // Go to next step
                    $("#step2").removeClass("active");
                    $("#step3").addClass("active");
                    currentStep = 3;
                    updateProgressBar();

                    btn.html(originalText);
                    btn.prop("disabled", false);
                } else {
                    $("#otpError")
                    .text("Invalid verification code. Please try again.")
                    .removeClass("hidden");
                    btn.html(originalText);
                    btn.prop("disabled", false);
                    return;
                }

            }else{
                $("#otpError")
                .text(result.message)
                .removeClass("hidden");
                btn.html(originalText);
                btn.prop("disabled", false);
                return;
            }
        })
    .catch((error) => console.error(error));
    }, 1000);
    


    // Simulate API call
    // setTimeout(() => {
    // // Go to next step
    // $("#step2").removeClass("active");
    // $("#step3").addClass("active");
    // currentStep = 3;
    // updateProgressBar();

    // // Reset button
    // btn.html(originalText);
    // btn.prop("disabled", false);
    // }, 1500);
});

// Handle password form submission
$("#passwordForm").submit(function (e) {
    e.preventDefault();
    const password = $("#newPassword").val();
    const confirmPassword = $("#confirmPassword").val();
    const email = $("#email").val().trim();


    if (!password || password.length < 6) {
    $("#passwordError")
        .text("Password must be at least 8 characters")
        .removeClass("hidden");
    return;
    }

    if (!/[A-Z]/.test(password)) {
    $("#passwordError")
        .text("Password must contain at least one uppercase letter")
        .removeClass("hidden");
    return;
    }

    if (!/[0-9]/.test(password)) {
    $("#passwordError")
        .text("Password must contain at least one number")
        .removeClass("hidden");
    return;
    }

    if (!/[^A-Za-z0-9]/.test(password)) {
    $("#passwordError")
        .text("Password must contain at least one special character")
        .removeClass("hidden");
    return;
    }

    if (password !== confirmPassword) {
    $("#passwordError")
        .text("Passwords do not match")
        .removeClass("hidden");
    return;
    }

    // Show loading state
    const btn = $(this).find("button[type='submit']");
    const originalText = btn.html();
    btn.html(`
    <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
    </svg>
    Resetting...
    `);
    btn.prop("disabled", true);

    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    

    const raw = JSON.stringify({
    "email": email,
    "password": password,
    });

    const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
    redirect: "follow"
    };

    setTimeout(() => {
        fetch("http://localhost:8080/api/v1/raillankapro/auth/reset/password", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            if (result.code !== 200) {
                const message = result.message;
                $("#passwordError")
                .text(message)
                .removeClass("hidden");
                btn.html(originalText);
                btn.prop("disabled", false);
                return; 
            }else{
                console.log(result);
                $("#step3").removeClass("active");
                $("#step4").addClass("active");
                currentStep = 4;
                updateProgressBar();

                // Reset button
                btn.html(originalText);
                btn.prop("disabled", false);
            }   
        })
        .catch((error) => console.error(error));

    }, 1500);

});

// Resend OTP
$("#resendCode").click(function (e) {
    e.preventDefault();

    const email = $("#userEmail").text().trim();

    const link = $(this);
    const originalText = link.text();
    link.text("Sending...");

    const requestOptions = {
    method: "POST",
    redirect: "follow"
    };

    
    fetch(`http://localhost:8080/api/v1/raillankapro/auth/reset/password/verify?email=${email}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            if (result.code != 200) {
                 const message = result.message;
                $("#otpError")
                .text(data)
                .removeClass("hidden");
                btn.html(originalText);
                btn.prop("disabled", false);
                return; 
            }
        })
    .catch((error) => console.error(error));
  

    setTimeout(() => {
    link.text(originalText);
    }, 1000);
});

// Navigation back to previous steps
$(".back-to-email").click(function (e) {
    e.preventDefault();
    $("#step2").removeClass("active");
    $("#step1").addClass("active");
    currentStep = 1;
    updateProgressBar();
    $("#emailError").addClass("hidden").text("");
});

$(".back-to-otp").click(function (e) {
    e.preventDefault();
    $("#step3").removeClass("active");
    $("#step2").addClass("active");
    currentStep = 2;
    updateProgressBar();
    $("#otpError").addClass("hidden").text("");
});



// Helper function
function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Initialize progress bar
updateProgressBar();
});