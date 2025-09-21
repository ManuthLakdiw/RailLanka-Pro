$(document).ready(function() {

let token = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');

    if (token) {
        let userName = localStorage.getItem('userName') || sessionStorage.getItem('userName'); // lowercase n
        // Update both username displays
        $('#adminUsername').text(userName);
        $('#tokenUsername').text(userName);
    } else {
        // If no token, fallback username or redirect
        $('#adminUsername, #tokenUsername').text('Guest');
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
    $('#logoutButton').on('click', function(e) {
        e.preventDefault();
        showLogoutModel();

        
    });

    function clearAllTokens() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userName');
        sessionStorage.removeItem('accessToken');
        sessionStorage.removeItem('userName');
        sessionStorage.removeItem('refreshToken');
    }


    $("#adminEmail").text(localStorage.getItem('email'))

const accessToken = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");

    if (!accessToken) {

        window.location.href = "../../logging-expired.html";
        

    }

const stringOnlyPattern = /^[A-Za-z\s]+$/;
    setupStaffProfileModal();
    setupChangePasswordModal();
    setupEditProfileModal();




const revenueCtx = $("#revenueChart")[0].getContext("2d");
new Chart(revenueCtx, {
    type: "bar",
    data: {
    labels: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
    datasets: [
        {
        label: "Revenue (LKR)",
        data: [
            850000, 920000, 780000, 1120000, 1250000, 980000, 1320000,
        ],
        backgroundColor: "rgba(59, 130, 246, 0.7)",
        borderColor: "rgb(37, 99, 235)",
        borderWidth: 1,
        borderRadius: 6,
        },
    ],
    },
    options: {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: { display: false },
        tooltip: {
        callbacks: {
            label: function (context) {
            return "LKR " + context.parsed.y.toLocaleString();
            },
        },
        },
    },
    scales: {
        y: {
        beginAtZero: true,
        ticks: {
            callback: function (value) {
            return "LKR " + (value / 1000).toFixed(0) + "K";
            },
        },
        },
    },
    },
});

const modal = $("#stationModal");
const addBtn = $("#addStationBtn");
const closeBtn = $("#closeModal");
const cancelBtn = $("#cancelBtn");

addBtn.on("click", function () {
    modal.addClass("active");
});

function closeModal() {
    modal.removeClass("active");
    resetFields();
}

closeBtn.on("click", closeModal);
cancelBtn.on("click", closeModal);

modal.on("click", function (e) {
    if (e.target === this) {
        modal.removeClass("active");
    }
});

$("#stationRegisterForm input").on("keydown", function (e) {
    if (e.key === "Enter") {
        e.preventDefault();

    }

});

$("#stationName").on("keypress", function (e) {
    const char = String.fromCharCode(e.which);


    if (!stringOnlyPattern.test(char)) {
        e.preventDefault(); 
    }
});

$("#stationCode").on("keypress", function (e) {
    const char = String.fromCharCode(e.which);

    if (!stringOnlyPattern.test(char)) {
        e.preventDefault(); 
    }
});


// suggest location

let autocomplete;
const inputLocation = $("#stationLocation")[0];

function initAutocomplete() {
    autocomplete = new google.maps.places.Autocomplete(inputLocation, {
    componentRestrictions: { country: "lk" },
    fields: [
    "place_id",
    "geometry",
    "name",
    "formatted_address",
    "address_components",
    "types",
    ],
    types: ["(regions)"],
});

    autocomplete.addListener("place_changed", onPlaceChanged);
}

function onPlaceChanged() {
    const place = autocomplete.getPlace();
    if (!place.geometry) {
        return;
    }

    const data = extractVillageAndProvince(place);
    $("#stationProvince").val(data.province);
    $("#stationDistrict").val(data.district);
}

function extractVillageAndProvince(place) {
    const result = {
    village: null,
    district: null,
    province: null,
    city: null,
    };

    if (place.address_components) {
        place.address_components.forEach((comp) => {
            const types = comp.types;
            if (
                types.includes("locality") ||
                types.includes("sublocality") ||
                types.includes("sublocality_level_1")
            )
                result.village = result.village || comp.long_name;

            if (types.includes("administrative_area_level_2"))
                result.district = comp.long_name;

            if (types.includes("administrative_area_level_1"))
                result.province = comp.long_name;
        });
    }

    if (!result.village && result.city) result.village = result.city;
    return result;
}

/////////////////////

$("#stationRegisterForm").on("submit", function (e) {
    e.preventDefault();

      if (!$("#stationDistrict").val().trim() || !$("#stationProvince").val().trim()) {
        const location = $("#stationLocation").val().trim();
        const splitLocationData = location.split(",").map(s => s.trim());

        if (splitLocationData.length === 2) {
            $("#stationDistrict").val(splitLocationData[0]);
            $("#stationProvince").val(splitLocationData[1]);
        } else if (splitLocationData.length === 3) {
            $("#stationDistrict").val(splitLocationData[0]);
            $("#stationProvince").val(splitLocationData[1]);
        } else {
            console.warn("Unexpected location format: " + location);
        }
    }

    const stationName = $("#stationName").val().trim();
    const stationCode = $("#stationCode").val().trim();
    const stationDistrict = $("#stationDistrict").val().trim();
    console.log("station district : " + stationDistrict)
    const stationProvince = $("#stationProvince").val().trim();
    console.log("station province : " + stationProvince)
    const platformSelection = $("#platformNumbersSelection").val();
    const platformLength = $("#platformLength").val().trim();

    let facilities = [];
    $("input[name='facilities']:checked").each(function() {
        facilities.push($(this).val());
    });
    facilities = facilities.join(", ");


    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTI4NzY3MCwiZXhwIjoxMDc1NTI4NzY3MH0.vxaERuJqs5vXzaP6iChN3Glid-ziQFuHx49YCsxRTys");

    const raw = JSON.stringify({
        "name": stationName,
        "stationCode": stationCode,
        "district": stationDistrict,
        "province": stationProvince,
        "noOfPlatforms": platformSelection,
        "platformLength": platformLength,
        "otherFacilities": facilities
    });

    const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
    redirect: "follow"
    };

    fetch("http://localhost:8080/api/v1/raillankapro/station/register", requestOptions)
    .then((response) => response.json())
    .then((result) => {
        console.log(result);
        if (result.code === 409) {
            $("#stationNameMsg").text(result.message).addClass("text-red-500")
        }

        if (result.code === 201) {
            toastr.success(result.data);
            closeModal();

        }
    })
    .catch((error) => console.error(error));



});



function resetFields() {
    $("#stationName").val("");
    $("#stationCode").val("");
    $("#stationDistrict").val("");
    $("#stationProvince").val("");
    $("#platformNumbersSelection").val("");
    $("#platformLength").val("");
    $("#stationLocation").val("")

    $("input[name='facilities']").prop("checked", false);
    $("#stationNameMsg").text("").removeClass("text-red-500")

       
}

$("#stationCode").on("input", function () {
      let val = $(this).val();

      val = val.replace(/[^A-Za-z]/g, "");

      val = val.toUpperCase();

      if (val.length > 3) {
        val = val.slice(0, 3);
      }

      $(this).val(val);
});

$("#stationCode").on("keypress", function (e) {
    if ($(this).val().length >= 3) {
        e.preventDefault(); // block extra typing
    }
});



function setupStaffProfileModal() {
  // Open staff profile modal
  $('#staffProfileButton').on('click', function(e) {
    e.preventDefault();
    fetchStaffData();
    $('#staffProfileModal').removeClass('hidden');
    setTimeout(() => {
      $('#staffProfileModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
    }, 10);
  });
  
  // Close staff profile modal
  $('#closeStaffProfileModal').on('click', function() {
    $('#staffProfileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
    setTimeout(() => {
      $('#staffProfileModal').addClass('hidden');
    }, 300);
  });
  
  function fetchStaffData() {

    const requestOptions = {
    method: "GET",
    redirect: "follow"
    };

    const userName = localStorage.getItem("userName") || sessionStorage.getItem("userName");
    fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/admin/get/by?username=${userName}`, requestOptions)
    .then(({result}) => {
        console.log(result)
        if (result.code === 200) {
            const staffData = result.data;  
            $('#staffId').text(staffData.id);
            $('#staffFirstName').text(staffData.firstname);
            $('#staffLastName').text(staffData.lastname);
            $('#staffEmail').text(staffData.email);
            $('#staffContact').text(staffData.phoneNumber);
            $('#staffRole').text(staffData.role);
            $('#staffStation').text(staffData.railwayStation);
            $('#staffDepartment').text("Management");
            $('#staffJoinDate').text(staffData.joinDate);
            $('#lastLogin').text(`Last login: ${localStorage.getItem("loginTime") || sessionStorage.getItem("loginTime")}`);
        }
    })
    .catch((error) => console.error(error));
       

    }
    }

    // Function to setup Change Password Modal
function setupChangePasswordModal() {
  // Open change password modal
  $('#staffChangePasswordButton').on('click', function() {
    $('#changePasswordModal').removeClass('hidden');
    setTimeout(() => {
      $('#changePasswordModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
    }, 10);
  });
  
  // Close change password modal
  $('#closeChangePasswordModal, #cancelChangePassword').on('click', function() {
    $('#changePasswordModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
    setTimeout(() => {
      $('#changePasswordModal').addClass('hidden');
      $('#changePasswordForm')[0].reset();
    }, 300);
  });
  
  // Toggle password visibility
  $('.toggle-password').on('click', function() {
    const target = $(this).data('target');
    const input = $('#' + target);
    const icon = $(this).find('i');
    
    if (input.attr('type') === 'password') {
      input.attr('type', 'text');
      icon.removeClass('fa-eye').addClass('fa-eye-slash');
    } else {
      input.attr('type', 'password');
      icon.removeClass('fa-eye-slash').addClass('fa-eye');
    }
  });
  
  $('#saveNewPassword').on('click', function() {
    const userName = localStorage.getItem("userName") || sessionStorage.getItem("userName");
    const button = $(this);
    const originalText = button.html();
    
    const currentPassword = $('#currentPassword').val();
    const newPassword = $('#newPassword').val();
    const confirmPassword = $('#confirmPassword').val();
    
    if (!currentPassword || !newPassword || !confirmPassword) {
      showError('Please fill in all password fields', 'error');
      return;
    }
    
    if (newPassword !== confirmPassword) {
      showError('New passwords do not match', 'error');
      return;
    }
    
    if (newPassword.length < 6) {
      showError('Password must be at least 8 characters long', 'error');
      return;
    }

    button.html('<i class="fas fa-spinner fa-spin mr-2"></i> Updating...');
    button.prop('disabled', true);

   

    const raw = JSON.stringify({
    "currentPassword": currentPassword,
    "newPassword": newPassword,
    "username": userName
    });

    const requestOptions = {
    method: "PUT",
    headers: {
        "Content-Type": "application/json"
    },
    body: raw,
    redirect: "follow"
    };

    fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/admin/change/password", requestOptions)
    .then(({result}) => {
        console.log(result)
        if (result.code === 200) {
            if (result.data) {
                $("#staffLoginError").addClass("hidden");
               setTimeout(() => {
                button.html('<i class="fas fa-check mr-2"></i> Updated!');
                
                setTimeout(() => {
                    $('#changePasswordModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
                    setTimeout(() => {
                    $('#changePasswordModal').addClass('hidden');
                    $('#changePasswordForm')[0].reset();
                    button.html(originalText);
                    button.prop('disabled', false);
                    }, 300);
                }, 1000);
                showLogoutModel();
                }, 1500);
            }else {
                showError('Current password is incorrect');
            } 
        }
    })
    .catch((error) => console.error(error));
    
    
    
  });
}

function setupEditProfileModal() {

  $('#editStaffProfileButton').on('click', function() {
     const requestOptions = {
    method: "GET",
    redirect: "follow"
    };

    const userName = localStorage.getItem("userName") || sessionStorage.getItem("userName");
    fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/admin/get/by?username=${userName}`, requestOptions)
    .then(({result}) => {
        console.log(result)
        if (result.code === 200) {
            const staffData = result.data;  
            $('#editFirstName').val(staffData.firstname);
            $('#editLastName').val(staffData.lastname);
            $('#editEmail').val(staffData.email);
            $('#editContact').val(staffData.phoneNumber);
            $('#editUsername').val(staffData.userName);
            
        }
    })
    .catch((error) => console.error(error));
       
    
    $('#editProfileModal').removeClass('hidden');
    setTimeout(() => {
      $('#editProfileModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
    }, 10);
  });
  
  // Close edit profile modal
  $('#closeEditProfileModal, #cancelEditProfile').on('click', function() {
    $('#editProfileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
    setTimeout(() => {
      $('#editProfileModal').addClass('hidden');
    }, 300);
  });
  
  $('#saveProfileChanges').on('click', function() {
    const button = $(this);
    const originalText = button.html();
    
    button.html('<i class="fas fa-spinner fa-spin mr-2"></i> Saving...');
    button.prop('disabled', true);
    
    const formData = {
      title: $('#editTitle').val(),
      firstName: $('#editFirstName').val(),
      lastName: $('#editLastName').val(),
      email: $('#editEmail').val(),
      contact: $('#editContact').val()
    };
    
    // Simulate API call (replace with actual API call)
    setTimeout(() => {
      // Update profile display with new values
      $('#staffTitle').text(formData.title);
      $('#staffFirstName').text(formData.firstName);
      $('#staffLastName').text(formData.lastName);
      $('#staffEmail').text(formData.email);
      $('#staffContact').text(formData.contact);
      
      // Show success message
      button.html('<i class="fas fa-check mr-2"></i> Saved!');
      
      // Close modal after delay
      setTimeout(() => {
        $('#editProfileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
        setTimeout(() => {
          $('#editProfileModal').addClass('hidden');
          button.html(originalText);
          button.prop('disabled', false);
          
          // Show success notification
          showAlert('Profile updated successfully!', 'success');
        }, 300);
      }, 1000);
    }, 1500);
  });
}

 function showError(message) {
        $("#staffLoginError").text(message).removeClass("hidden");
        $("#staffLoginError").addClass(
        "animate__animated animate__headShake"
        );
        setTimeout(() => {
        $("#staffLoginError").removeClass("animate__headShake");
        }, 1000);
    }

    function showLogoutModel(){
        const logoutOverlay = $('<div>').addClass('fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50');
        const logoutModal = $('<div>').addClass('bg-white rounded-lg p-6 max-w-sm mx-4 text-center transform scale-95 opacity-0 transition-all duration-300 shadow-xl');

        logoutModal.html(`
            <div class="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Securing Your Session</h3>
            <p class="text-gray-600">Please wait while we securely sign you out...</p>
            <div class="mt-4 w-full bg-gray-200 rounded-full h-1.5">
                <div class="bg-blue-600 h-1.5 rounded-full progress-bar" style="width: 0%"></div>
            </div>
        `);

        logoutOverlay.append(logoutModal);
        $('body').append(logoutOverlay);

        setTimeout(() => {
            logoutModal.removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
            
            $('.progress-bar').animate({ width: '100%' }, 1800);
        }, 10);

        setTimeout(() => {
           clearAllTokens();
            setTimeout(() => {
                logoutModal.removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
                logoutOverlay.fadeOut(400, function() {
                    $(this).remove();
                    window.location.href = '../pages/anim.html';
                });
            }, 200);
        }, 1800);
    }




});