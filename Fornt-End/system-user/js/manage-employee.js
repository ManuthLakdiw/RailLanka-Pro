$(document).ready(function() {

    toastr.options = {
          closeButton: true,
          progressBar: true,
          positionClass: "toast-top-right",
          timeOut: 2000,
    };

    let currentPage = 1;
    let totalPages = 0;
    const maxVisiblePages = 5;
    let currentKeyword = "";
    let category = "all"

    fetchEmployees(currentPage);
    loadStationForSelection("stationFilter",null)

    const employeeModal = $("#employeeModal");
    const employeeUpdateModal = $("#updateEmployeeModal")
    const stationEmployeesModal = $("#stationEmployeesModal");
    const addEmployeeBtn = $("#addEmployeeBtn");
    const closeModalBtns = $("#closeModal, #cancelBtn, #closeStationModal, #closeStationModalBtn, #closeUpdateModal, #cancelUpdateBtn");


    addEmployeeBtn.on("click", function () {
        employeeModal.addClass("active");
        loadStationForSelection("employeeStation",null);
    });

    closeModalBtns.on("click", function () {
        employeeModal.removeClass("active");
        employeeUpdateModal.removeClass("active")
        stationEmployeesModal.removeClass("active");
        resetRegisterModelFields();
        $("#stationFilter").val("")
    });

    $(window).on("click", function (e) {
        if (e.target === employeeModal.get(0)) {
            employeeModal.removeClass("active");   
        }
        if (e.target === stationEmployeesModal.get(0)) {
            stationEmployeesModal.removeClass("active");
            $("#stationFilter").val("")
        }
    });

    function resetRegisterModelFields() {
        $("#employeeFirstname").val("");
        $("#employeeLastname").val("");
        $("#employeeNIC").val("");
        $("#employeeDOB").val("");
        $("#employeeContactNumber").val("");
        $("#employeeEmail").val("");
        $("#employeeAddress").val("");
        $("#employeeRole").val("");
        $("#employeeStation").val("");
    }

    // function loadStationForSelection(selectedStation = null) {
    //     const myHeaders = new Headers();
    //     myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk3MTA0MywiZXhwIjoxMDc1NTk3MTA0M30.es7C1MO8rHMFWIK70lOaJmo1D0WGLe1_X9fnGGSbeEg");

    //     const requestOptions = {
    //         method: "GET",
    //         headers: myHeaders,
    //         redirect: "follow"
    //     };

    //     fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions)
    //         .then((response) => response.json())
    //         .then((result) => {
    //             console.log(result);

    //             if (result.code === 200) {
    //                 const allStations = result.data;
    //                 allStations.sort((a, b) => a.name.localeCompare(b.name));

    //                 $("#employeeStation").empty();
    //                 $("#employeeStation").empty();

    //                 $("#employeeStation")
    //                     .append('<option disabled selected value="">Select a Station</option>');

    //                 allStations.forEach(station => {
    //                     let optionHtml; 
    //                     if (selectedStation && selectedStation === station.name) {
    //                         optionHtml = `<option value="${station.name}" selected>${station.name} (${station.stationCode})</option>`;
    //                     } else {
    //                         optionHtml = station.inService
    //                             ? `<option value="${station.name}">${station.name} (${station.stationCode})</option>`
    //                             : `<option disabled value="">${station.name} (${station.stationCode}) - out-of-service</option>`;
    //                     }
    //                     $("#employeeStation").append(optionHtml);
    //                 });
    //             }
    //         })
    //         .catch((error) => console.error(error));
    // } 

    function loadStationForSelection(dropdownId, selectedStation = null) {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjkwODk2NSwiZXhwIjoxMDc1NjkwODk2NX0.zRFdZrhuDN5WwKjVUtr7BS9Z1o9uE-obA2_H_bpy9tw");

        const requestOptions = {
            method: "GET",
            headers: myHeaders,
            redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.code === 200) {
                    const allStations = result.data;
                    allStations.sort((a, b) => a.name.localeCompare(b.name));

                    const dropdown = $(`#${dropdownId}`);
                    dropdown.empty();

                    dropdown.append('<option disabled selected value="">Select a Station</option>');

                    allStations.forEach(station => {
                        let optionHtml; 
                        if (selectedStation && selectedStation === station.name) {
                            optionHtml = `<option value="${station.name}" selected>${station.name} (${station.stationCode})</option>`;
                        } else {
                            optionHtml = station.inService
                                ? `<option value="${station.name}">${station.name} (${station.stationCode})</option>`
                                : `<option disabled value="">${station.name} (${station.stationCode}) - out-of-service</option>`;
                        }
                        dropdown.append(optionHtml);
                    });
                }
            })
            .catch((error) => console.error(error));
    }

    function fetchEmployees(page, keyword = null , position = null) {
        let url = "";
        if (position) {
            url = `http://localhost:8080/api/v1/raillankapro/employee/filter/by/position/${page}/7?position=${position}`;
        }else if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/employee/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/employee/getall/${page}/7`;
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

            const employees = result.data;
            totalPages = result.totalPages;

            $('#currentPage').text(result.startNumber);
            $('#totalPage').text(result.totalItems);
            $("#selectedLastRowData").text(result.endNumber);

            $("#employeeTable tbody").empty();

            if (employees.length === 0) {
                $("#employeeTable tbody").append(
                    `<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">
                        No Employees found
                    </td></tr>`
                );
                return;
            }
            
            employees.forEach(employee => {
                let statusText = employee.active ? "active" : "inactive";
                let statusClass = employee.active ? "status-active" : "status-inactive";
                const parts = employee.station.split(",");
                let color;
                let position = employee.position;
                let formattedPosition = position
                    .toLowerCase()
                    .split("_")
                    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ");

                if (position === "DEPUTY_STATION_MASTER") {
                    color = "indigo"; 
                } else if (position === "STATION_ASSISTANT") {
                    color = "sky"; 
                } else if (position === "TICKET_CHECKING_CLERK") {
                    color = "emerald"; 
                } else if (position === "ANNOUNCEMENT_OFFICER") {
                    color = "amber"; 
                } else if (position === "PORTER") {
                    color = "slate"; 
                } else if (position === "SIGNALMAN") {
                    color = "yellow"; 
                } else if (position === "CUSTOMER_SERVICE") {
                    color = "cyan"; 
                } else if (position === "SECURITY") {
                    color = "rose"; 
                }

                const stationName = parts[0];   
                const stationCode = parts[1];   
            
                $("#employeeTable tbody").append(
                    `
                    <tr
                        class="hover:bg-gray-50"
                        >
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                            <div
                                class="flex-shrink-0 h-10 w-10 rounded-full bg-${color}-100 flex items-center justify-center"
                            >
                                <i class="fas fa-user text-${color}-600"></i>
                            </div>
                            <div class="ml-4">
                                <div class="text-sm font-medium text-gray-900">
                                ${employee.firstname} ${employee.lastname}
                                </div>
                                <span class ="text-sm text-gray-500">ID:</span>
                                <div class="text-sm text-gray-500 inline">${employee.employeeId}</div>
                            </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm text-gray-900">
                            <a href="tel:${employee.contactNumber}" class="text-gray-900">
                                ${employee.contactNumber}
                            </a>
                        </div>
                        <div class="text-sm text-gray-500">
                            <a href="mailto:${employee.email}" class="text-blue-600">
                                ${employee.email}
                            </a>
                        </div>
                            
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                                <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs leading-4 font-medium bg-${color}-100 text-${color}-800" > ${formattedPosition}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">${stationName}</div>
                            <div class="text-sm text-gray-500">${stationCode}</div>
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
                            <button id="updateEmployeeBtn" class="text-blue-600 hover:text-blue-900" data-id="${employee.employeeId}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button id="deleteEmployeeBtn" class="text-red-600 hover:text-red-900" data-id="${employee.employeeId}">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="text-gray-600 hover:text-gray-900">
                                <i class="fas ${employee.active ? "fa-toggle-on" : "fa-toggle-off"}"></i>
                            </button>
                            </div>
                        </td>
                    </tr>
                    `
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

    $("#filterEmployee").on("input", function () {
        $("#roleFilter").val("all")
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchEmployees(currentPage, currentKeyword, null);
    });

    $("#searchEmpStaff").on("input" , function() {
        let station = $("#stationFilter").val();
        $("#empStaffPositionSelection").val("");
        let keyword = $(this).val();
    
        fetchEmployees(currentPage,null,null)

        fetchStaff(station,null,keyword);

    });

    $("#roleFilter").on("change" , function () {
        const currentCategory= $("#roleFilter").val();
        if(currentCategory === "") {
            fetchEmployees(currentPage)
            return;
        }
        category = currentCategory; 
        currentPage = 1;
        fetchEmployees(currentPage, null , category);

    })

    $("#empStaffPositionSelection").on("change" , function () {
        let station = $("#stationFilter").val();
        console.log(station);
        
        let position = $(this).val();

        if (position === "") {
            fetchStaff(station,null)
            return;
        }

        fetchStaff(station,position)

    })

    $("#stationFilter").on("change" , function () {
        $("#stationEmployeesModal").addClass("active")
        let station = $(this).val();
        $("#stationModalTitle").text(`${station} Station Employees`)
        $("#empStaffPositionSelection").val("")
        fetchStaff(station , null)

    })

    function fetchStaff(station = null , position = null , keyword = null) {
        let url = `http://localhost:8080/api/v1/raillankapro/station/getall/staff/by?station=${station}`;

        if (position) {
            url += `&position=${position}`;
        }

        if (keyword && keyword.length >= 2) {
            url += `&keyword=${keyword}`;
        }

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjkyMjUxMiwiZXhwIjoxMDc1NjkyMjUxMn0.qhxgW70xGeKDnW5_csiQvLiDJIjvx-l7UhFpjRxtN5E");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(url , requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 200) {
                fetchEmployees(currentPage)
                const staffList = result.data; 
                const container = $("#stationEmployeesList"); 
                container.empty();

                if (staffList.length != 0) {
                    $("#stationModalSubtitle").text(`${staffList.length} employees assigned to this station`)
                    // $("#searchEmpStaff").removeClass("hidden")
                    // $("#empStaffPositionSelection").removeClass("hidden")
                    // $("#searchIco").removeClass("hidden")
                }else {
                    $("#stationModalSubtitle").text(`No employees assigned to this station`)
                    // $("#searchEmpStaff").addClass("hidden")
                    // $("#empStaffPositionSelection").addClass("hidden")
                    //  $("#searchIco").addClass("hidden")
                    const emptyState = `
                        <div class="flex flex-col items-center justify-center py-12 px-4 text-center">
                            <div class="w-24 h-24 rounded-full bg-blue-100 flex items-center justify-center mb-6">
                                <i class="fas fa-user-slash text-blue-500 text-4xl"></i>
                            </div>
                            <h3 class="text-xl font-semibold text-gray-700 mb-2">No Employees Found</h3>
                            <p class="text-gray-500 max-w-md">
                                There are currently no employees assigned to ${station} station.
                                Please check back later or contact administration.
                            </p>
                        </div>
                    `;
                    container.append(emptyState);
                }

                staffList.forEach(staff => {
                    let staffColor;
                    let formattedEmployeePosition = staff.position
                    .toLowerCase()
                    .split("_")
                    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ");
 
                    if (staff.position === "DEPUTY_STATION_MASTER") {
                        staffColor = "indigo"; 
                    } else if (staff.position === "STATION_ASSISTANT") {
                        staffColor = "sky"; 
                    } else if (staff.position === "TICKET_CHECKING_CLERK") {
                        staffColor = "emerald"; 
                    } else if (staff.position === "ANNOUNCEMENT_OFFICER") {
                        staffColor = "amber"; 
                    } else if (staff.position === "PORTER") {
                        staffColor = "slate"; 
                    } else if (staff.position === "SIGNALMAN") {
                        staffColor = "yellow"; 
                    } else if (staff.position === "CUSTOMER_SERVICE") {
                        staffColor = "cyan"; 
                    } else if (staff.position === "SECURITY") {
                        staffColor = "rose"; 
                    }else if (staff.position === "COUNTER") {
                        staffColor = "fuchsia"; 
                    }else if (staff.position === "STATION_MASTER") {
                        staffColor = "lime"; 
                    }

                    const staffDiv = `
                        <div
                            class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm flex items-center justify-between"
                            >
                            <div class="flex items-center">
                                <div
                                class="w-12 h-12 rounded-full bg-${staffColor}-100 flex items-center justify-center mr-4"
                                >
                                <i class="fas fa-user text-${staffColor}-600"></i>
                                </div>
                                <div>
                                <h4 class="font-bold text-gray-800">${staff.name}</h4>
                                <div class="flex items-center mt-1">
                                    <span
                                    class="bg-${staffColor}-100 text-${staffColor}-800 text-xs px-2 py-1 rounded-full"
                                    >${formattedEmployeePosition}</span
                                    >
                                    <span class="mx-2 text-gray-400">•</span>
                                    <span class="text-sm text-gray-600">ID: ${staff.id}</span>
                                </div>
                                </div>
                            </div>
                            <div class="text-right">
                                <p class="text-sm text-gray-500">${staff.telephone}</p>
                                <p class="text-sm text-gray-500">${staff.email}</p>
                            </div>
                        </div>
                    `;

                    container.append(staffDiv);

                })

            }
        })
        .catch((error) => console.error(error));

    }

    $("#employeeRegistrationForm").on("submit", function (e) {
        e.preventDefault();

        const employeeFirstname = $("#employeeFirstname").val();
        const employeeLastname = $("#employeeLastname").val();
        const employeeNIC = $("#employeeNIC").val();
        const employeeDOB = $("#employeeDOB").val();
        const employeeContactNumber = $("#employeeContactNumber").val();
        const employeeEmail = $("#employeeEmail").val();
        const employeeAddress = $("#employeeAddress").val();
        const employeeRole = $("#employeeRole").val();
        const employeeStation = $("#employeeStation").val();

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjgyNTc3OCwiZXhwIjoxMDc1NjgyNTc3OH0.WPBqOlfaeLD6D6yOqAB0M-wC9k28NN2053BY8XcxO90");

        const raw = JSON.stringify({
            "firstname": employeeFirstname,
            "lastname": employeeLastname,
            "idNumber": employeeNIC,
            "contactNumber": employeeContactNumber,
            "position": employeeRole,
            "station": employeeStation,
            "dateOfBirth": employeeDOB,
            "email": employeeEmail,
            "address":employeeAddress
        });

        const requestOptions = {
            method: "POST",
            headers: myHeaders,
            body: raw,
            redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/employee/register", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code != 201) {
                toastr.warning(result.message)
                return;
            }

            if (result.code === 201) {
                toastr.success(result.data)
                employeeModal.removeClass("active");
                resetRegisterModelFields();
                fetchEmployees(currentPage)



            }
        })
        .catch((error) => console.error(error));

    });

    $("#employeeUpdateForm").on("submit" , function (e) {
        e.preventDefault;

        const employeeId = $("#updateEmployeeId").val();
        const firstName = $("#updateEmployeeFirstname").val();
        const lastName = $("#updateEmployeeLastname").val();
        const nic = $("#updateEmployeeNIC").val();
        const dob = $("#updateEmployeeDOB").val();
        const contactNumber = $("#updateEmployeeContactNumber").val();
        const email = $("#updateEmployeeEmail").val();
        const address = $("#updateEmployeeAddress").val();
        const role = $("#updateEmployeeRole").val();
        const station = $("#updateEmployeeStation").val();
        const statusValue = $("input[name='updateStatus']:checked").val();  
        const boolChecked = (statusValue === "true");

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1Njk4MzM4MSwiZXhwIjoxMDc1Njk4MzM4MX0.IZXH8px-C5D1hzk87isH5X-CTzJnp9vJ3SX4BCVpoPI");

        const raw = JSON.stringify({
        "employeeId": employeeId,
        "firstname": firstName,
        "lastname": lastName,
        "idNumber": nic,
        "contactNumber": contactNumber,
        "position": role,
        "station": station,
        "dateOfBirth": dob,
        "email": email,
        "address": address,
        "active": boolChecked
        });

        const requestOptions = {
        method: "PUT",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/employee/update", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if(result.code != 200) {
                toastr.warning(result.message);
                return;
            }

            if(result.code === 200) {
                toastr.success(result.data)
                employeeUpdateModal.removeClass("active")
                fetchEmployees(currentPage)


            }
        })
        .catch((error) => console.error(error));
            })

    $(document).on("click", "#updateEmployeeBtn", function () {
        employeeUpdateModal.addClass("active")
        const id = $(this).data("id");

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjkyMjUxMiwiZXhwIjoxMDc1NjkyMjUxMn0.qhxgW70xGeKDnW5_csiQvLiDJIjvx-l7UhFpjRxtN5E");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/employee/getemployee?id=${id}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code === 200) {
                const data = result.data;
                $("#updateEmployeeId").val(data.employeeId)
                $("#updateEmployeeFirstname").val(data.firstname)
                $("#updateEmployeeLastname").val(data.lastname)
                $("#updateEmployeeNIC").val(data.idNumber)
                $("#updateEmployeeDOB").val(data.dateOfBirth)
                $("#updateEmployeeContactNumber").val(data.contactNumber)
                $("#updateEmployeeEmail").val(data.email)
                $("#updateEmployeeAddress").val(data.address)
                $("#updateEmployeeRole").val(data.position)
                loadStationForSelection("updateEmployeeStation",data.station)

                if (data.active) {
                    $("#activeRadio").prop("checked", true);
                }else {
                    $("#inactiveRadio").prop("checked", true);
                }
            
            }
        })
        .catch((error) => console.error(error));
            });

    $(document).on("click", "#deleteEmployeeBtn", function () {
        
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

                fetch(`http://localhost:8080/api/v1/raillankapro/employee/delete?id=${id}`, requestOptions)
                .then((response) => response.json())
                .then((result) => {
                        fetchEmployees(currentPage)
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

        const employeeId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim(); 

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NTk0NTM4MywiZXhwIjoxMDc1NTk0NTM4M30.ymySMHJcEdMcymm49RPRttY_yszNx44_WFZ1SJWgAkI");

        const requestOptions = {
            method: "PUT",
            headers: myHeaders,
            redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/employee/changestatus/${employeeId}/${newStatus}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code === 200) {
                fetchEmployees(currentPage);
                if (result.data) {
                    toastr.success(result.message);
                }else {
                    toastr.warning(result.message);  
                }

            }
        })
        .catch((error) => console.error(error));

    });


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
            fetchEmployees(currentPage, currentKeyword);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchEmployees(currentPage, currentKeyword);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchEmployees(currentPage, currentKeyword);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchEmployees(currentPage, currentKeyword);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchEmployees(currentPage, currentKeyword);
    }
    });

    $("#exportBtn").on("click" , function() {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1Njk4MzM4MSwiZXhwIjoxMDc1Njk4MzM4MX0.IZXH8px-C5D1hzk87isH5X-CTzJnp9vJ3SX4BCVpoPI");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/pdf/getemployees/by/station?station=Batapola", requestOptions)
        .then((response) => response.blob())
        .then((result) => {
            const url = window.URL.createObjectURL(result);

            // <a> tag එක dynamically හදලා click කරනවා
            const a = document.createElement("a");
            a.href = url;
            a.download = "employees.pdf"; // ගොනුවේ නම
            document.body.appendChild(a);
            a.click();

            // Clean up
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch((error) => console.error(error));
            })

});
  

