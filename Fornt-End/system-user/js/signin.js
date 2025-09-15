$(document).ready(function () {
    // Toggle password visibility
    $("#toggleStaffPassword").click(function () {
        const passwordField = $("#staffPassword");
        const type =
        passwordField.attr("type") === "password" ? "text" : "password";
        passwordField.attr("type", type);

        // Change icon
        $(this).html(
        type === "password"
            ? '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>'
            : '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" /></svg>'
        );
    });

    $("#staffLoginForm").submit(function (e) {
        e.preventDefault();

        $("#staffLoginError").addClass("hidden");

        const username = $("#staffUsername").val().trim();
        const password = $("#staffPassword").val().trim();

        if (!username || !password) {
        showError("Please enter both Username and Password");
        return;
        }

        const submitBtn = $("#staffLoginForm button[type='submit']");
        const originalText = submitBtn.html();
        submitBtn.html(`
        <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Authenticating...
        `);
        submitBtn.prop("disabled", true);

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        const raw = JSON.stringify({ username, password });

        const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow",
        };

        fetch(
        "http://localhost:8080/api/v1/raillankapro/auth/login",
        requestOptions
        )
        .then((response) => response.json())
        .then((result) => {
            console.log(result);

            if (result.code === 401) {
                showError(
                    result.message || "Invalid credentials. Please try again."
                );
            } else if (result.code === 200) {
                const accessToken = result.data.accessToken;
                const refreshToken = result.data.refreshToken;
                const userName = result.data.username;
                
                
                if ($("#rememberDevice").is(":checked")) {
                    localStorage.setItem("accessToken", accessToken);
                    localStorage.setItem("refreshToken", refreshToken);
                    localStorage.setItem("userName", userName )
                } else {
                    sessionStorage.setItem("accessToken", accessToken);
                    sessionStorage.setItem("refreshToken", refreshToken);
                    sessionStorage.setItem("userName", userName )

                }

                window.location.href = "/system-user/index.html ";
            }
        })
        .catch((error) => {
            console.error(error);
            showError("Something went wrong. Please try again.");
        })
        .finally(() => {
            // Reset button
            submitBtn.html(originalText);
            submitBtn.prop("disabled", false);
        });
    });


    function showError(message) {
        $("#staffLoginError").text(message).removeClass("hidden");
        $("#staffLoginError").addClass(
        "animate__animated animate__headShake"
        );
        setTimeout(() => {
        $("#staffLoginError").removeClass("animate__headShake");
        }, 1000);
    }

    // Input focus effects
    $(".input-field")
        .focus(function () {
        $(this).addClass("input-highlight");
        $(this).next(".floating-label").addClass("text-blue-600");
        })
        .blur(function () {
        $(this).removeClass("input-highlight");
        $(this).next(".floating-label").removeClass("text-blue-600");
        });
});