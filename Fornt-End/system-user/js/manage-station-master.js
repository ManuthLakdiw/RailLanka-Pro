$(document).ready(function () {
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

    // Toggle status button functionality
    $(".fa-toggle-on, .fa-toggle-off").on("click", function () {
        const $row = $(this).closest("tr");
        const $statusCell = $row.find("td:nth-child(5)");
        const $statusSpan = $statusCell.find("span");

        if ($(this).hasClass("fa-toggle-on")) {
            $(this).removeClass("fa-toggle-on").addClass("fa-toggle-off");
            $statusSpan.text("Inactive")
                    .removeClass("status-active")
                    .addClass("status-inactive");
        } else {
            $(this).removeClass("fa-toggle-off").addClass("fa-toggle-on");
            $statusSpan.text("Active")
                    .removeClass("status-inactive")
                    .addClass("status-active");
        }
    });
    
    
    //////////////////////////////////////////// load station for selection box ////////////////////////////////////////////////////////////
    function loadStationNamesWithCodes() {

        const myHeaders = new Headers();
        myHeaders.append("Authorization", 
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTU5OTIxMCwiZXhwIjoxMDc1NTU5OTIxMH0.fMuIkZFjmOqiz1qhaTckVlcoVIaZdIP7hphhIodzrvw");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        let selectedStations = [];


        fetch("http://localhost:8080/api/v1/raillankapro/stationmaster/getall/assigned/stations", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            selectedStations = result;
            console.log(selectedStations)
        })
        .catch((error) => console.error(error));

        fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code === 200) {
                const allStations = result.data;
                allStations.sort((a, b) => a.name.localeCompare(b.name));
                 $("#smasterStationSelection").empty();
                 $("#smasterStationSelection").append(
                    `<option disabled selected value="">
                         Select a Station
                    </option>`
                    
                 );

                allStations.forEach(station => {
                    if (selectedStations.some(s => s.trim().toLowerCase() === station.name.trim().toLowerCase())) {
                        $("#smasterStationSelection").append(
                            `<option disabled value="">
                                ${station.name} (${station.stationCode} - assigned)
                            </option>`
                        );
                        return;
                    }

                    if (!station.inService) {
                        $("#smasterStationSelection").append(
                        `<option disabled  value="">
                                ${station.name} (${station.stationCode} - out-of-service)
                         </option>`
                        
                        );
                        return;
                    }
                    $("#smasterStationSelection").append(
                        `<option value="${station.name}">
                                ${station.name} (${station.stationCode})
                         </option>`
                    );
                });
            }
        })
        .catch((error) => console.error(error));

    }

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
        const smasterStationSelection = $("#smasterStationSelection").val().trim();

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
                fetchSmasters(currentPage);
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
    function fetchSmasters(page, keyword = "") {
        let url = "";
        if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/stationmaster/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/stationmaster/getall/${page}/7`;
        }
        
        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTY3NTI4MSwiZXhwIjoxMDc1NTY3NTI4MX0.TOojMUE9rxEDSm_Oykf4WCsyZIlGdwXVgPXMUf-QdQg");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(url, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

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
                        <button class="text-blue-600 hover:text-blue-900">
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
            })

            updatePaginationControls(page, totalPages);
        })
        .catch((error) => {
            console.log(error);
            $("#stationTable tbody").html(
              '<tr><td colspan="6" class="px-6 py-4 text-center text-red-500">Error loading data</td></tr>'
            );
        });      
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
                confirmButton: 'bg-blue-600 hover:bg-blue-700 text-white  py-2 px-4 rounded',
                cancelButton: 'bg-gray-300 hover:bg-gray-400 text-black  py-2 px-4 rounded ml-2'
            },
            buttonsStyling: false ,
            confirmButtonText: "Yes, delete it!"
        }).then((result) => {
            if (result.isConfirmed) {

                const myHeaders = new Headers();
                myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTQ2MzMyNywiZXhwIjoxMDc1NTQ2MzMyN30.u5VC6wqDHzYRHHjFMEOYIb5iu_mrYdo17JeYRddG4s0");

                const requestOptions = {
                    method: "PUT",
                    headers: myHeaders,
                    redirect: "follow"
                };

                fetch(`http://localhost:8080/api/v1/raillankapro/stationmaster/delete?id=${id}`, requestOptions)
                .then((response) => response.json())
                .then((result) => {
                        fetchSmasters(currentPage)
                        fetchSmasters(currentPage);
                        console.log(result);
                        Swal.fire({
                        title: "Deleted!",
                        text: result.data,
                        icon: "success",
                        showConfirmButton: false,   
                        timer: 1300  
                        });
                }).catch((error) => {
                        console.error(error);
                        Swal.fire({
                            title: "Error!",
                            text: "Something went wrong while deleting.",
                            icon: "error"
                        });
                });
            }
        }).catch((error) => {
            console.error(error);
            Swal.fire({
                title: "Error!",
                text: "Something went wrong while deleting.",
                icon: "error"
            });
});
    });

    ///////////////////////////////////////// change status ////////////////////////////////////////////////////////////
    $(document).on("click", ".fa-toggle-on, .fa-toggle-off", function () {
        const btn = $(this);
        const row = btn.closest("tr");
        const statusSpan = row.find("td:nth-child(5) span");

        let newStatus;
        if (btn.hasClass("fa-toggle-on")) {
            btn.removeClass("fa-toggle-on").addClass("fa-toggle-off");
            newStatus = false;
        } else {
            btn.removeClass("fa-toggle-off").addClass("fa-toggle-on");
            newStatus = true;
        }

        const smasterId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim(); 

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTY3NTI4MSwiZXhwIjoxMDc1NTY3NTI4MX0.TOojMUE9rxEDSm_Oykf4WCsyZIlGdwXVgPXMUf-QdQg");

        const requestOptions = {
        method: "PUT",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/stationmaster/changestatus/${smasterId}/${newStatus}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code === 200) {
                fetchSmasters(currentPage)
                $("#filterSmaster").val("")
                if (result.data) {
                    toastr.success(result.message);
                }else {
                    toastr.warning(result.message);  
                }
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
        $("#smasterStationSelection").val(""); 
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