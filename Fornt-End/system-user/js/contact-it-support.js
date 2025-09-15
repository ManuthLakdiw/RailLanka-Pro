$(document).ready(function () {

    let token = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');

    if (token) {
        let userName = localStorage.getItem('userName') || sessionStorage.getItem('userName'); // lowercase n
        $('#adminUsername').text(userName);
        $('#tokenUsername').text(userName);
    } else {
        $('#adminUsername, #tokenUsername').text('Guest');
    }

    $('#logoutButton').on('click', function(e) {
        e.preventDefault();
        clearAllTokens();
        window.location.href = '../pages/anim.html';
    });

    $("#adminEmail").text(localStorage.getItem('email'))

    const accessToken = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
        if (!accessToken) {
            window.location.href = "../../logging-expired.html"; 
        }


    function clearAllTokens() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userName');
        sessionStorage.removeItem('accessToken');
        sessionStorage.removeItem('userName');
        sessionStorage.removeItem('refreshToken');
    }

    async function refreshAccessToken() {
        const refreshToken = localStorage.getItem('refreshToken') || sessionStorage.getItem('refreshToken');
        
        if (!refreshToken) {
            console.log('❌ No refresh token found');
            window.location.href = "../../logging-expired.html";
            return null;
        }

        try {
            console.log('🔄 Sending request to refresh token API...');

            const response = await fetch('http://localhost:8080/api/v1/raillankapro/auth/refreshtoken', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    token: refreshToken
                })
            });

            const result = await response.json();
            console.log('📥 Refresh token response:', result);

            if (response.ok && result.code === 200) {
                const newAccessToken = result.data.accessToken;
                const newRefreshToken = result.data.refreshToken;
                
                if (localStorage.getItem('refreshToken')) {
                    localStorage.setItem('accessToken', newAccessToken);
                    localStorage.setItem('refreshToken', newRefreshToken);
                } else if (sessionStorage.getItem('refreshToken')) {
                    sessionStorage.setItem('accessToken', newAccessToken);
                    sessionStorage.setItem('refreshToken', newRefreshToken);
                }

                console.log('✅ Token refreshed successfully:', newAccessToken);
                return newAccessToken;
            } else {
                console.log('⚠️ Refresh token expired or invalid:', result.message);
                clearAllTokens();
                window.location.href = "../../logging-expired.html";
                return null;
            }
        } catch (error) {
            console.error('🔥 Error refreshing token:', error);
            clearAllTokens();
            window.location.href = "../../logging-expired.html";
            return null;
        }
    }

    async function fetchWithTokenRefresh(url, options = {}) {
        let accessToken = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');
        
        if (!accessToken) {
            console.log("❌ No access token found. Redirecting...");
            window.location.href = "../../logging-expired.html";
        }

        const headers = {
            ...options.headers,
            'Authorization': `Bearer ${accessToken}`
        };

        const requestOptions = {
            ...options,
            headers: headers
        };

        try {
            console.log("➡️ Sending API request with token:", accessToken);
            let response = await fetch(url, requestOptions);
            let result = await response.json();

            console.log("📥 API response:", result);

            if (result.code === 401 && result.message === "JWT token has expired") {
                console.log('⚠️ Access token expired, attempting to refresh...');
                
                // Try to refresh token
                const newToken = await refreshAccessToken();
                
                if (newToken) {
                    console.log("🔄 Retrying API call with new token:", newToken);
                    // Retry the original request with new token
                    const retryHeaders = {
                        ...options.headers,
                        'Authorization': `Bearer ${newToken}`
                    };
                    
                    const retryOptions = {
                        ...options,
                        headers: retryHeaders
                    };
                    
                    response = await fetch(url, retryOptions);
                    result = await response.json();

                    console.log("📥 Retried API response:", result);
                }
            }

            return { response, result };
            } catch (error) {
                console.error('🔥 Fetch error:', error); // Debug: network or fetch error
                throw error;
            }
    }


    // Handle form submission
    $("#supportForm").submit(function (e) {
        e.preventDefault();

        const requesterName = $("#requesterName").val();
        const requesterEmail = $("#requesterEmail").val();
        const subject = $("#subject").val();
        const description = $("#description").val();

        const category = $('input[name="category"]:checked').val(); // System issue , software, account , other
        const priority = $('input[name="priority"]:checked').val(); // low , medium , high , Cretical

        const attachments = $("#attachment")[0].files;

        if (!subject || !description || !category || !priority || !requesterEmail || !requesterName) {
            return;
        }

        // Simulate form submission
        const ticketId = "RL-" + Math.floor(1000 + Math.random() * 9000);
        $("#ticketId").text("#" + ticketId);

        // Show success modal
        $("#successModal").fadeIn();
    });

    // Close modal
    $("#closeModal").click(function () {
        $("#successModal").fadeOut();
        $("#supportForm")[0].reset();
    });

    // File upload handling
    $("#attachment").change(function () {
        const files = $(this)[0].files;
        if (files.length > 0) {
        $(".fa-cloud-upload-alt")
            .removeClass("fa-cloud-upload-alt")
            .addClass("fa-file-alt text-blue-500");
        $('p:contains("Click to upload")').html(
            '<span class="font-semibold">' +
            files.length +
            " file(s) selected</span>"
        );
        }
    });

    $(".knowledge-toggle").click(function () {
        const content = $(this).next(".knowledge-content");
        const isExpanded = content.hasClass("expanded");

        $(".knowledge-content").removeClass("expanded");
        $(".knowledge-toggle").removeClass("expanded");

        if (!isExpanded) {
        content.addClass("expanded");
        $(this).addClass("expanded");
        }
    });

    $("#clearBtn").on("click" , function(){
        resetForm();
    });

    function resetForm() {
        $("#supportForm")[0].reset();
        $(".fa-file-alt")
            .removeClass("fa-file-alt text-blue-500")
            .addClass("fa-cloud-upload-alt");
        $('p:contains("file(s) selected")').html(
            'Click to upload or drag and drop'
        );
    }
      
});