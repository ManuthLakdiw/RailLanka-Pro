$(document).ready(function () {

    toastr.options = {
          closeButton: true,
          progressBar: true,
          positionClass: "toast-bottom-right",
          timeOut: 2000,
    };

    let currentPage = 1;
    let totalPages = 0;
    const maxVisiblePages = 5;
    let lastColorIndex = -1; 
    let currentKeyword = "";

    loadStationForSelection();
    fetchCounters(currentPage)

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
        resetRegisterForm();


    });

    $(window).on("click", function (event) {
        if ($(event.target).is(staffModal)) {
        staffModal.removeClass("active");
        }
        if ($(event.target).is(staffUpdateModal)) {
        staffUpdateModal.removeClass("active");
        }
    });


    ////////////////////////////// password validation/ /////////////////////////////
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

    ////////////////////////////// password show and hide //////////////////////////////
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


    ////////////////////////////// load station for selction /////////////////////////////
    function loadStationForSelection(selectedStation = null) {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk3MTA0MywiZXhwIjoxMDc1NTk3MTA0M30.es7C1MO8rHMFWIK70lOaJmo1D0WGLe1_X9fnGGSbeEg");

        const requestOptions = {
            method: "GET",
            headers: myHeaders,
            redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions)
            .then((response) => response.json())
            .then((result) => {
                console.log(result);

                if (result.code === 200) {
                    const allStations = result.data;
                    allStations.sort((a, b) => a.name.localeCompare(b.name));

                    $(".stationSelection").empty();

                    $(".stationSelection")
                        .append('<option disabled selected value="">Select a Station</option>');

                    allStations.forEach(station => {
                        let optionHtml; 
                        if (selectedStation && selectedStation === station.name) {
                            optionHtml = `<option value="${station.name}" selected>${station.name} (${station.stationCode})</option>`;
                        } else {
                            optionHtml = station.inService
                                ? `<option value="${station.name}">${station.name} (${station.stationCode})</option>`
                                : `<option disabled value="">${station.name} (${station.stationCode}) - out-of-service</option>`;
                        }
                        $(".stationSelection").append(optionHtml);
                    });
                }
            })
            .catch((error) => console.error(error));
    }

    ////////////////////////////// load counters by station  //////////////////////////////
    function loadCountersByStation(stationName, selectedCounter = null) {
    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk3NjgxNiwiZXhwIjoxMDc1NTk3NjgxNn0.KCpLq2zIVR9xb_h0epn6QWebdasoZHIRwwweDJmuEDw");

    const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
    };

    fetch(`http://localhost:8080/api/v1/raillankapro/counter/get/counternumbers/by/stationname/${stationName}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result);

            const disabledCounters = result.data;
            const counters = ["COUNTER_1", "COUNTER_2", "COUNTER_3", "NONE"];

            const $counterSelect = $(".counterNumberSelection");
            $counterSelect.empty();
            $counterSelect.append(`<option disabled value="">Select a Counter</option>`);

            counters.forEach(c => {
                const label = c.replace("_", " ");
                let option;

                if (c === "NONE") {
                    option = `<option value="${c}">${capitalizeFirst(label)}</option>`;
                } else {
                    const isDisabled = disabledCounters.includes(c);

                    if (isDisabled && c !== selectedCounter) {
                        option = `<option value="${c}" disabled>${capitalizeFirst(label)} - already assigned</option>`;
                    } else {
                        option = `<option value="${c}">${capitalizeFirst(label)}</option>`;
                    }
                }

                $counterSelect.append(option);
            });

            // set selected counter for update modal
            if (selectedCounter) {
                $counterSelect.val(selectedCounter);
            } else {
                $counterSelect.val("");
            }
        })
        .catch((error) => console.error(error));
    }

    ////////////////////////////// catch selection event //////////////////////////////
    $(".stationSelection").on("change", function () {
        let selectedStationValue = $(this).val();
        loadCountersByStation(selectedStationValue);
    });


    ////////////////////////////// load update form  //////////////////////////////
    $(document).on("click", "#updateCounterBtn", function () {
        staffUpdateModal.addClass("active");

        const id = $(this).data("id");

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk3NjgxNiwiZXhwIjoxMDc1NTk3NjgxNn0.KCpLq2zIVR9xb_h0epn6QWebdasoZHIRwwweDJmuEDw");

        const requestOptions = {
            method: "GET",
            headers: myHeaders,
            redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/counter/getcounter?id=${id}`, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.code === 200) {
                    $("#updateStaffId").val(result.data.id);
                    $("#updateStaffFirstname").val(result.data.firstname);
                    $("#updateStaffLastname").val(result.data.lastname);
                    $("#updateStaffNIC").val(result.data.idNumber);
                    $("#updateStaffDOB").val(result.data.dob);
                    $("#updateStaffContactNumber").val(result.data.phoneNumber);
                    $("#updateStaffEmail").val(result.data.email);
                    $("#updateStaffAddress").val(result.data.address);
                    $("#updateStaffUsername").val(result.data.userName);
                    $("#updateStaffYearsOfExperience").val(result.data.yearsOfExperience);
                    loadStationForSelection(result.data.railwayStation);
                    loadCountersByStation(result.data.railwayStation, result.data.counterNumber);

                    if (result.data.active) {
                        $("#activeRadio").prop("checked", true);
                    } else {
                        $("#inactiveRadio").prop("checked", true);
                    }
                }
            })
            .catch((error) => console.error(error));

            
    });
    



    ////////////////////////////// register counter //////////////////////////////
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
        const staffStationSelection = $("#staffModal .stationSelection").val();
        const staffCounterSelection = $("#staffModal .counterNumberSelection").val();

        console.log(staffStationSelection)
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
                resetRegisterForm();
                toastr.success(result.data);
                staffModal.removeClass("active");
                fetchCounters(currentPage);
            }
        })
        .catch((error) => console.error(error));


    });

    ////////////////////////////// update counter //////////////////////////////
    $("#staffUpdateForm").on("submit" , function (e) {
        e.preventDefault();

        const id = $("#updateStaffId").val().trim();
        const staffFirstname = $("#updateStaffFirstname").val().trim();
        const staffLastname = $("#updateStaffLastname").val().trim();
        const staffNIC = $("#updateStaffNIC").val().trim();
        const staffDOB = $("#updateStaffDOB").val().trim();
        const staffContactNumber = $("#updateStaffContactNumber").val().trim();
        const staffEmail = $("#updateStaffEmail").val().trim();
        const staffAddress = $("#updateStaffAddress").val().trim();
        const staffYearsOfExperience = $("#updateStaffYearsOfExperience").val().trim();
        const staffStationSelection = $("#staffUpdateModal .stationSelection").val();
        const staffCounterSelection = $("#staffUpdateModal .counterNumberSelection").val();
        const statusValue = $("input[name='updateStatus']:checked").val();  
        const boolChecked = (statusValue === "true");

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk3NzgyOSwiZXhwIjoxMDc1NTk3NzgyOX0.wgPCJSCQQn05EAWt75yt5vliN8CFO2QZUIMnnh0jjyk");

        const raw = JSON.stringify({
        "id": id,
        "firstname": staffFirstname,
        "lastname": staffLastname,
        "idNumber": staffNIC,
        "phoneNumber": staffContactNumber,
        "railwayStation": staffStationSelection,
        "counterNumber": staffCounterSelection,
        "dob": staffDOB,
        "email": staffEmail,
        "address": staffAddress,
        "yearsOfExperience": staffYearsOfExperience,
        "active": boolChecked
        });

        const requestOptions = {
        method: "PUT",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/counter/update", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code === 200) {
                $("#staffUpdateModal").removeClass("active");
                toastr.success(result.data);
                fetchCounters(currentPage)
                loadStationForSelection()
            }
        })
        .catch((error) => console.error(error));      
    })

    ///////////////////////////// delete counter //////////////////////////////
    $(document).on("click", "#deleteCounterBtn", function () {
        
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

                fetch(`http://localhost:8080/api/v1/raillankapro/counter/delete?id=${id}`, requestOptions)
                .then((response) => response.json())
                .then((result) => {
                        fetchCounters(currentPage)
                        loadStationForSelection();
                         $("#filterStaff").val("")
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

    ////////////////////////////// load counter //////////////////////////////
    function fetchCounters(page, keyword = "") {
        let url = "";
        if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/counter/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/counter/getall/${page}/7`;
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

            const counters = result.data;
            totalPages = result.totalPages;

            $('#currentPage').text(result.startNumber);
            $('#totalPage').text(result.totalItems);
            $("#selectedLastRowData").text(result.endNumber);

            $("#staffTable tbody").empty();

            if (counters.length === 0) {
                $("#staffTable tbody").append(
                  `<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">
                     No Counters found
                   </td></tr>`
                );
                return;
            }
            
            counters.forEach(counter => {
                const randomColor = getRandomColor();
                let statusText = counter.active ? "active" : "inactive";
                let statusClass = counter.active ? "status-active" : "status-inactive";
                const parts = counter.railwayStation.split(",");
                const formattedCounter = counter.counterNumber.replace("_", " "); 

                const stationName = parts[0];   
                const stationCode = parts[1];   
            
                $("#staffTable tbody").append(
                    `<tr class="hover:bg-gray-50">
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="flex items-center">
                                <div
                                    class="flex-shrink-0 h-10 w-10 rounded-full bg-${randomColor}-100 flex items-center justify-center"
                                >
                                    <i class="fas fa-user text-${randomColor}-600"></i>
                                </div>
                                <div class="ml-4">
                                    <div class="text-sm font-medium text-gray-900">
                                    ${counter.firstname} ${counter.lastname}
                                    </div>
                                    <span class ="text-sm text-gray-500">ID:</span>
                                    <div class="text-sm text-gray-500 inline">${counter.id}</div>
                                </div>
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm text-gray-900">${counter.phoneNumber}</div>
                                <div class="text-sm text-gray-500">${counter.email}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm text-gray-900">
                                ${stationName} - ${formattedCounter.charAt(0).toUpperCase() + formattedCounter.slice(1).toLowerCase()}
                                </div>
                                <div class="text-sm text-gray-500">${stationCode}-${counter.counterNumber.replace("COUNTER_", "C")}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                ${counter.yearsOfExperience} year(s)
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
                                <button id="updateCounterBtn" class="text-blue-600 hover:text-blue-900" data-id="${counter.id}">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button id = "deleteCounterBtn" class="text-red-600 hover:text-red-900" data-id="${counter.id}">
                                    <i class="fas fa-trash"></i>
                                </button>
                                <button class="text-gray-600 hover:text-gray-900">
                                    <i class="fas ${counter.active ? "fa-toggle-on" : "fa-toggle-off"}"></i>
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

    //////////////////////////////  counter status change //////////////////////////////
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

            const counterId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim(); 

            const myHeaders = new Headers();
            myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk0NTM4MywiZXhwIjoxMDc1NTk0NTM4M30.ymySMHJcEdMcymm49RPRttY_yszNx44_WFZ1SJWgAkI");

            const requestOptions = {
                method: "PUT",
                headers: myHeaders,
                redirect: "follow"
            };

            fetch(`http://localhost:8080/api/v1/raillankapro/counter/changestatus/${counterId}/${newStatus}`, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                console.log(result)
                if (result.code === 200) {
                    loadStationForSelection();
                    fetchCounters(currentPage);
                    $("#filterStaff").val("")
                    if (result.data) {
                        toastr.success(result.message);
                    }else {
                        toastr.warning(result.message);  
                    }

                }
            })
            .catch((error) => console.error(error));

        });

    //////////////////////////////  filter counter //////////////////////////////
    $("#filterStaff").on("input", function () {
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchCounters(currentPage, currentKeyword);
    });


    ////////////////////////////// pagiantions //////////////////////////////
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
            fetchCounters(currentPage, currentKeyword);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchCounters(currentPage, currentKeyword);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchCounters(currentPage, currentKeyword);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchCounters(currentPage, currentKeyword);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchCounters(currentPage, currentKeyword);
    }
    });



    function resetRegisterForm() {
        $("#staffFirstname").val("");
        $("#staffLastname").val("");
        $("#staffNIC").val("");
        $("#staffDOB").val("");
        $("#staffContactNumber").val("");
        $("#staffEmail").val("");
        $("#staffAddress").val("");
        $("#staffUsername").val("");
        $("#staffPassword").val("");
        $("#staffYearsOfExperience").val("");
        $(".stationSelection").val("");
        $(".counterNumberSelection").val("");

        $("#staffUsernameMsg").text("").removeClass("text-red-500")

    }


    function getRandomColor() {
        let index;
        do {
            index = Math.floor(Math.random() * iconColors.length);
        } while (index === lastColorIndex); 

        lastColorIndex = index;
        return iconColors[index];
    }


    function capitalizeFirst(label) {
        return label.charAt(0).toUpperCase() + label.slice(1).toLowerCase();
    }
 
});