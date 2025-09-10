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


    toastr.options = {
          closeButton: true,
          progressBar: true,
          positionClass: "toast-top-right",
          timeOut: 2000,
    };
    console.log("manage-station-master js loaded");

    let currentPage = 1;
    let totalPages = 0;
    const maxVisiblePages = 5;
    let lastColorIndex = -1; 
    let currentKeyword = "";

    fetchSmasters(currentPage);
    loadStationNamesWithCodes();



    const modal = $("#masterModal");
    const addBtn = $("#addMasterBtn");
    const closeBtn = $("#closeModal");
    const cancelBtn = $("#cancelBtn");

    // Open register modal
    addBtn.on("click", function () {
        modal.addClass("active");
        loadStationNamesWithCodes();
    });

    // Close register modal
    function closeModal() {
        modal.removeClass("active");
        resetRegisterForm();
    }

    closeBtn.on("click", closeModal);
    cancelBtn.on("click", closeModal);

    // Close register  modal if clicked outside
    modal.on("click", function (e) {
        if (e.target === this) {
            modal.removeClass("active");
        }
    });

    // // Toggle status button functionality
    // $(".fa-toggle-on, .fa-toggle-off").on("click", function () {
    //     const $row = $(this).closest("tr");
    //     const $statusCell = $row.find("td:nth-child(5)");
    //     const $statusSpan = $statusCell.find("span");

    //     if ($(this).hasClass("fa-toggle-on")) {
    //         $(this).removeClass("fa-toggle-on").addClass("fa-toggle-off");
    //         $statusSpan.text("Inactive")
    //                 .removeClass("status-active")
    //                 .addClass("status-inactive");
    //     } else {
    //         $(this).removeClass("fa-toggle-off").addClass("fa-toggle-on");
    //         $statusSpan.text("Active")
    //                 .removeClass("status-inactive")
    //                 .addClass("status-active");
    //     }
    // });
    
    
    //////////////////////////////////////////// load station for selection box ////////////////////////////////////////////////////////////
    async function loadStationNamesWithCodes(selectedStation = null) {
        try {
            // Fetch assigned stations
            const { response: assignedResponse, result: assignedResult } = await fetchWithTokenRefresh(
                "http://localhost:8080/api/v1/raillankapro/stationmaster/getall/assigned/stations",
                { method: "GET", redirect: "follow" }
            );

            if (!assignedResponse || !assignedResult) return;

            const selectedStations = assignedResult.map(s => s.trim().toLowerCase());

            // Fetch all stations with names and codes
            const { response: allStationsResponse, result } = await fetchWithTokenRefresh(
                "http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes",
                { method: "GET", redirect: "follow" }
            );

            if (!allStationsResponse || !result) return;

            if (result.code === 200) {
                const allStations = result.data;
                allStations.sort((a, b) => a.name.localeCompare(b.name));

                const $select = $(".smasterStationSelection");
                $select.empty();
                $select.append('<option disabled selected value="">Select a Station</option>');

                allStations.forEach(station => {
                    const stationNameLower = station.name.trim().toLowerCase();
                    let optionHtml = "";

                    if (selectedStations.includes(stationNameLower)) {
                        if (selectedStation && selectedStation.trim().toLowerCase() === stationNameLower) {
                            optionHtml = `<option value="${station.name}" selected>${station.name} (${station.stationCode})</option>`;
                        } else {
                            optionHtml = `<option disabled value="${station.name}">${station.name} (${station.stationCode} - assigned)</option>`;
                        }
                    } else if (!station.inService) {
                        optionHtml = `<option disabled value="${station.name}">${station.name} (${station.stationCode} - out-of-service)</option>`;
                    } else {
                        optionHtml = `<option value="${station.name}">${station.name} (${station.stationCode})</option>`;
                    }

                    $select.append(optionHtml);
                });

                if (selectedStation) {
                    $select.val(selectedStation);
                }
            }

        } catch (error) {
            console.error(error);
        }
    }

    // async function loadStationNamesWithCodes(selectedStation = null) {
    //     const myHeaders = new Headers();
    //     myHeaders.append("Authorization", 
    //         "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTU5OTIxMCwiZXhwIjoxMDc1NTU5OTIxMH0.fMuIkZFjmOqiz1qhaTckVlcoVIaZdIP7hphhIodzrvw");

    //     const requestOptions = {
    //         method: "GET",
    //         headers: myHeaders,
    //         redirect: "follow"
    //     };

    //     try {
    //         const assignedResponse = await fetch("http://localhost:8080/api/v1/raillankapro/stationmaster/getall/assigned/stations", requestOptions);
    //         const assignedStations = await assignedResponse.json(); 
    //         console.log("Assigned stations:", assignedStations);

            
    //         const allResponse = await fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions);
    //         const allResult = await allResponse.json();
    //         console.log("All stations result:", allResult);

    //         if (allResult.code === 200) {
    //             const allStations = allResult.data;
    //             allStations.sort((a, b) => a.name.localeCompare(b.name));

    //             const $select = $(".smasterStationSelection");
    //             $select.empty();
    //             $select.append(`<option disabled selected value="">Select a Station</option>`);

    //             allStations.forEach(station => {
    //                 let optionHtml = "";

    //                 if (selectedStation) {
    //                         if (assignedStations.includes(selectedStation)) {
    //                     optionHtml = `<option enable value="${station.name}">
    //                                     ${station.name} (${station.stationCode})
    //                                 </option>`;
    //                 }
    //                 }

                    

    //                 if (assignedStations.includes(station.name)) {
    //                     optionHtml = `<option disabled value="${station.name}">
    //                                     ${station.name} (${station.stationCode} - assigned)
    //                                 </option>`;
    //                 } else if (!station.inService) {
    //                     optionHtml = `<option disabled value="${station.name}">
    //                                     ${station.name} (${station.stationCode} - out-of-service)
    //                                 </option>`;
    //                 } else {
    //                     optionHtml = `<option value="${station.name}">
    //                                     ${station.name} (${station.stationCode})
    //                                 </option>`;
    //                 }

    //                 $select.append(optionHtml);
    //             });

    //             console.log("Dropdown values:", $select.find("option").map(function(){ return $(this).val(); }).get());
    //         }
    //     } catch (error) {
    //         console.error(error);
    //     }
    // }


    ////////////////////////////////////////// password validation ////////////////////////////////////////////////////////////
    
    
    $("#smasterPassword").on("input", function () {
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

    ///////////////////////////////////////// password show and hide eye ////////////////////////////////////////////////////////////
    $("#toggleSmasterPassword").click(function () {
        const passwordField = $("#smasterPassword"); 
        const type = passwordField.attr("type") === "password" ? "text" : "password";
        passwordField.attr("type", type);

        $(this).html(
            type === "password"
            ? '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>'
            : '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" /></svg>'
        );

    });


    ///////////////////////////////////////// register station master ////////////////////////////////////////////////////////////
    $("#smasterRegistrationForm").on("submit" , e => {
        e.preventDefault();

        const smasterFirstname = $("#smasterFirstname").val().trim();
        const smasterLastname = $("#smasterLastname").val().trim();
        const smasterNIC = $("#smasterNIC").val().trim();
        const smasterDOB = $("#smasterDOB").val().trim();
        const smasterContactNumber = $("#smasterContactNumber").val().trim();
        const smasterEmail = $("#smasterEmail").val().trim();
        const smasterAddress = $("#smasterAddress").val().trim();
        const smasterUsername = $("#smasterUsername").val().trim();
        const smasterPassword = $("#smasterPassword").val().trim();
        const smasterYearsOfExperience = $("#smasterYearsOfExperience").val().trim();
        const smasterStationSelection = $("#masterModal .smasterStationSelection").val().trim();

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        const raw = JSON.stringify({
            "firstname": smasterFirstname,
            "lastname": smasterLastname,
            "userName": smasterUsername,
            "password": smasterPassword,
            "idNumber": smasterNIC,
            "phoneNumber": smasterContactNumber,
            "railwayStation": smasterStationSelection,
            "dob": smasterDOB,
            "email": smasterEmail,
            "address": smasterAddress,
            "yearsOfExperience": smasterYearsOfExperience
        });

        const requestOptions = {
            method: "POST",
            headers: myHeaders,
            body: raw,
            redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/auth/register/stationmaster", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code === 409) {
                $("#smasterUsernameMsg").text(result.message).addClass("text-red-500")
                return;
            }

            if (result.code === 201) {
                toastr.success(result.data);
                fetchSmasters(currentPage);
                loadStationNamesWithCodes();
                closeModal();

            }
        })
        .catch((error) => console.error(error));
    });


    ///////////////////////////////////////// pagination ////////////////////////////////////////////////////////////
    function updatePaginationControls(currentPage, totalPages) {
      const pageNumbers = $('#pageNumbers');
      pageNumbers.empty();

      $('#btnFirst, #btnBack').prop('disabled', currentPage === 1);
      $('#btnNext, #btnLast').prop('disabled', currentPage === totalPages);

      let startPage, endPage;
      if (totalPages <= maxVisiblePages) {
        startPage = 1;
        endPage = totalPages;
      } else {
        const maxPagesBeforeCurrent = Math.floor(maxVisiblePages / 2);
        const maxPagesAfterCurrent = Math.ceil(maxVisiblePages / 2) - 1;

        if (currentPage <= maxPagesBeforeCurrent) {
          startPage = 1;
          endPage = maxVisiblePages;
        } else if (currentPage + maxPagesAfterCurrent >= totalPages) {
          startPage = totalPages - maxVisiblePages + 1;
          endPage = totalPages;
        } else {
          startPage = currentPage - maxPagesBeforeCurrent;
          endPage = currentPage + maxPagesAfterCurrent;
        }
      }

   
      if (startPage > 1) {
        pageNumbers.append(createPageButton(1));
        if (startPage > 2) {
          pageNumbers.append('<span class="px-3 py-1">...</span>');
        }
      }

      for (let i = startPage; i <= endPage; i++) {
        pageNumbers.append(createPageButton(i));
      }

      if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
          pageNumbers.append('<span class="px-3 py-1">...</span>');
        }
        pageNumbers.append(createPageButton(totalPages));
      }
    }

    function createPageButton(pageNumber) {
        const isActive = pageNumber === currentPage;
        return $(`
            <button class="page-btn w-10 h-10 rounded-md border ${isActive ? 'active border-blue-200' : 'border-gray-300'}
            ${isActive ? 'bg-blue-600 text-white' : 'bg-white text-gray-700'}">
            ${pageNumber}
            </button>
        `).on('click', () => {
            if (pageNumber !== currentPage) {
            currentPage = pageNumber;
            fetchSmasters(currentPage, currentKeyword);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchSmasters(currentPage, currentKeyword);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchSmasters(currentPage, currentKeyword);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchSmasters(currentPage, currentKeyword);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchSmasters(currentPage, currentKeyword);
    }
    });
        

    ///////////////////////////////////////// load table ////////////////////////////////////////////////////////////
    async function fetchSmasters(page, keyword = "") {
        let url = "";
        if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/stationmaster/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/stationmaster/getall/${page}/7`;
        }

        try {
            const { response, result } = await fetchWithTokenRefresh(url, {
                method: "GET",
                redirect: "follow"
            });

            if (!response || !result) return;

            console.log(result);

            const smasters = result.data;
            totalPages = result.totalPages;

            $('#currentPage').text(result.startNumber);
            $('#totalPage').text(result.totalItems);
            $("#selectedLastRowData").text(result.endNumber);

            $("#smasterTable tbody").empty();

            if (smasters.length === 0) {
                $("#smasterTable tbody").append(
                `<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">
                    No station masters found
                </td></tr>`
                );
                return;
            }
            
            smasters.forEach(smaster => {
                const randomColor = getRandomColor();
                let statusText = smaster.active ? "active" : "inactive";
                let statusClass = smaster.active ? "status-active" : "status-inactive";
                const parts = smaster.railwayStation.split(",");
                const stationName = parts[0];   
                const stationCode = parts[1];   

                $("#smasterTable tbody").append(
                    `<tr class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="flex items-center">
                        <div
                            class="flex-shrink-0 h-10 w-10 rounded-full bg-${randomColor}-100 flex items-center justify-center"
                        >
                            <i class="fas fa-user-tie text-${randomColor}-600"></i>
                        </div>
                        <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">
                            ${smaster.firstname} ${smaster.lastname}
                            </div>
                            <span class ="text-sm text-gray-500">ID:</span>
                            <div class="text-sm text-gray-500 inline">${smaster.id}</div>
                        </div>
                        </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-gray-900">
                            <a href="tel:${smaster.phoneNumber}" class="text-gray-900">
                                ${smaster.phoneNumber}
                            </a>
                        </div>
                        <div class="text-sm text-gray-500">
                            <a href="mailto:${smaster.email}" class="text-blue-600">
                                ${smaster.email}
                            </a>
                        </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-gray-900">${stationName}</div>
                        <div class="text-sm text-gray-500">${stationCode}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        ${smaster.yearsOfExperience} year(s)
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <span
                        class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}"
                        >
                        ${statusText}
                        </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <div class="flex space-x-2">
                        <button id="updateSmasterBtn" class="text-blue-600 hover:text-blue-900" data-id="${smaster.id}">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button id = "deleteSmasterBtn" class="text-red-600 hover:text-red-900" data-id="${smaster.id}" >
                            <i class="fas fa-trash"></i>
                        </button>
                        <button class="text-gray-600 hover:text-gray-900">
                            <i class="fas ${smaster.active ? "fa-toggle-on" : "fa-toggle-off"}"></i>
                        </button>
                        </div>
                    </td>
                    </tr>`
                );
            });

            updatePaginationControls(page, totalPages);

        } catch (error) {
            console.log(error);
            $("#stationTable tbody").html(
            '<tr><td colspan="6" class="px-6 py-4 text-center text-red-500">Error loading data</td></tr>'
            );
        }      
    }



    ///////////////////////////////////////// delete station ////////////////////////////////////////////////////////////
    $(document).on("click", "#deleteSmasterBtn", function () {
        const id = $(this).data("id");

        Swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            icon: "warning",
            showCancelButton: true,
            customClass: {
                confirmButton: 'bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded',
                cancelButton: 'bg-gray-300 hover:bg-gray-400 text-black py-2 px-4 rounded ml-2'
            },
            buttonsStyling: false,
            confirmButtonText: "Yes, delete it!"
        }).then((result) => {
            if (result.isConfirmed) {
                const url = `http://localhost:8080/api/v1/raillankapro/stationmaster/delete?id=${id}`;

                fetchWithTokenRefresh(url, { method: "PUT" })
                    .then(({ result }) => {   
                        fetchSmasters(currentPage);
                        loadStationNamesWithCodes();
                        console.log(result);
                        toastr.success(result.data);
                    })
                    .catch((error) => {
                        console.error(error);
                        toastr
                    });
            }
        }).catch((error) => {
            console.error(error);
            toastr.error("An error occurred while deleting the station master.");
        });
    });


    ///////////////////////////////////////// change status ////////////////////////////////////////////////////////////
    $(document).on("click", ".fa-toggle-on, .fa-toggle-off", function () {
        const btn = $(this);
        const row = btn.closest("tr");

        let newStatus;
        if (btn.hasClass("fa-toggle-on")) {
            btn.removeClass("fa-toggle-on").addClass("fa-toggle-off");
            newStatus = false;
        } else {
            btn.removeClass("fa-toggle-off").addClass("fa-toggle-on");
            newStatus = true;
        }

        const smasterId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim(); 
        const url = `http://localhost:8080/api/v1/raillankapro/stationmaster/changestatus/${smasterId}/${newStatus}`;

        fetchWithTokenRefresh(url, { method: "PUT" })
            .then(({ result }) => {   
                console.log(result);
                if (result.code === 200) {
                    fetchSmasters(currentPage);
                    loadStationNamesWithCodes();
                    $("#filterSmaster").val("");

                    if (result.data) {
                        toastr.success(result.message);
                    } else {
                        toastr.warning(result.message);
                    }
                } else {
                    toastr.error(result.message || "Status update failed");
                }
            })
            .catch((error) => console.error(error));
    });



    ///////////////////////////////////////// filter ////////////////////////////////////////////////////////////
    $("#filterSmaster").on("input", function () {
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchSmasters(currentPage, currentKeyword);
    });


     /////////////////////////////////// update model ////////////////////////////////////////////////////////////////////////////////////////////////
    const updateModal = $("#smasterUpdateModal");
    const closeUpdateModelBtn = $("#closeUpdateModal");
    const cancelUpdateModelBtn = $("#cancelUpdateBtn");

    function closeUpdateModal() {
        updateModal.removeClass("active");
        resetRegisterForm();
        $('#updateSmasterId').val("")
    }

    closeUpdateModelBtn.on("click", closeUpdateModal);
    cancelUpdateModelBtn.on("click", closeUpdateModal);

    updateModal.on("click", function (e) {
        if (e.target === this) {
            updateModal.removeClass("active");
        }
    });

    $(document).on("click", "#updateSmasterBtn", function() {
        $("#smasterUpdateModal").addClass("active");

        const id = $(this).data("id");
        const url = `http://localhost:8080/api/v1/raillankapro/stationmaster/getstationmaster?id=${id}`;

        fetchWithTokenRefresh(url, { method: "GET" })
            .then(({ result }) => {
                console.log(result);
                let data = result.data;
                $('#updateSmasterId').val(data.id);
                $('#updateSmasterFirstname').val(data.firstname);
                $('#updateSmasterLastname').val(data.lastname);
                $('#updateSmasterNIC').val(data.idNumber);
                $('#updateSmasterDOB').val(data.dob);
                $('#updateSmasterContactNumber').val(data.phoneNumber);
                $('#updateSmasterEmail').val(data.email);
                $('#updateSmasterAddress').val(data.address);
                $('#updateSmasterUsername').val(data.userName);
                $('#updateSmasterYearsOfExperience').val(data.yearsOfExperience);
                
                // station dropdown load karanawa selected ekath set karanna
                loadStationNamesWithCodes(data.railwayStation.trim());

                if (data.active) {
                    $("#activeRadio").prop("checked", true);
                } else {
                    $("#inactiveRadio").prop("checked", true);
                }
            })
            .catch((error) => console.error(error));
    });


    $("#smasterUpdateForm").on("submit", function (e) {
        e.preventDefault();

        const smasterID = $('#updateSmasterId').val().trim();
        const smasterFisrtname = $('#updateSmasterFirstname').val().trim();
        const smasterLastName = $('#updateSmasterLastname').val().trim();
        const smasterNIC = $('#updateSmasterNIC').val().trim();
        const smasterDOB = $('#updateSmasterDOB').val().trim();
        const smasterContactNumber = $('#updateSmasterContactNumber').val().trim();
        const smasterEmail = $('#updateSmasterEmail').val().trim();
        const smasterAddress = $('#updateSmasterAddress').val().trim();
        const smasterYOE = $('#updateSmasterYearsOfExperience').val().trim();
        const smasterStation = $("#smasterUpdateForm .smasterStationSelection").val();
        const statusValue = $("input[name='updateStatus']:checked").val();
        const boolChecked = (statusValue === "true");

        const raw = JSON.stringify({
            "id": smasterID,
            "firstname": smasterFisrtname,
            "lastname": smasterLastName,
            "idNumber": smasterNIC,
            "phoneNumber": smasterContactNumber,
            "railwayStation": smasterStation,
            "dob": smasterDOB,
            "email": smasterEmail,
            "address": smasterAddress,
            "yearsOfExperience": smasterYOE,
            "active": boolChecked
        });

        const requestOptions = {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: raw,
            redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/stationmaster/update", requestOptions)
            .then(({ result }) => {
                console.log(result);
                $("#smasterUpdateModal").removeClass("active");

                if (result.code === 200) {
                    toastr.success(result.data);
                    fetchSmasters(currentPage);
                    loadStationNamesWithCodes();
                } else {
                    toastr.warning(result.message || "Update failed");
                }
            })
            .catch((error) => {
                console.error(error);
                toastr.error("Something went wrong while updating station master");
            });
    });



    /////////////////////////////////////

    function resetRegisterForm() {

        $("#smasterFirstname").val("");
        $("#smasterLastname").val("");
        $("#smasterNIC").val("");
        $("#smasterDOB").val("");
        $("#smasterContactNumber").val("");
        $("#smasterEmail").val("");
        $("#smasterAddress").val("");
        $("#smasterUsername").val("");
        $("#smasterPassword").val("");
        $("#smasterYearsOfExperience").val("");
        $(".smasterStationSelection").val(""); 
        $("#smasterUsernameMsg").text("").removeClass("text-red-500")


    };

    function getRandomColor() {
        let index;
        do {
            index = Math.floor(Math.random() * iconColors.length);
        } while (index === lastColorIndex); 

        lastColorIndex = index;
        return iconColors[index];
    }


   


});