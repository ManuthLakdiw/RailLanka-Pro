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

    let currentPage = 1;
    let totalPages = 0;
    const maxVisiblePages = 5;
    let currentKeyword = "";
    let lastColorIndex = -1; 

    fetchSchedule(currentPage);
    loadTrainsForSelection($("#trainFilter"),null,false);



    const addCreateSheduleBtn = $("#addScheduleBtn");
    const closeCreateSheduleButton = $("#closeModal , #cancelBtn")
    const closeViewDetailsButton = $("#closeScheduleDetailsModal, #closeViewBtn")
    const closeUpdateModalButton = $("#closeUpdateModal, #cancelUpdateBtn")

    addCreateSheduleBtn.on("click" , function() {
        openModal("createScheduleModal")
        loadTrainsForSelection($("#trainSelection"));

    })

    closeCreateSheduleButton.on("click" , function() {
        closeModal("createScheduleModal")
        resetScheduleForm();
    });

    closeUpdateModalButton.on("click" , function() {
        closeModal("updateScheduleModal")
        updateErroMsgReset();
    });


    closeViewDetailsButton.on("click" , function() {
        closeModal("scheduleModal")
    });

    function closeModal(modalId){
        $(`#${modalId}`).removeClass("active")
    }

    function openModal(modalId){
        $(`#${modalId}`).addClass("active")
    }

    $("#trainSelection").on("change", function () {
        const selectedTrain = $(this).val();
        loadStationForSelectionUsingTrain(
            $("#departureStationSelection"),
            selectedTrain
        );

        $("#arrivalStationSelection").empty().append(
            '<option disabled selected value="">Select Departure First</option>'
        );

        $("#stopsContainer").empty();

        $("#addStopBtn").click();

    });

    $("#updateTrainSelection").on("change", function () {
        const selectedTrain = $(this).val();

        // Reset departure stations
        loadStationForSelectionUsingTrain(
            $("#updateDepartureStationSelection"),
            selectedTrain
        );

        // Reset arrival stations
        $("#updateArrivalStationSelection").empty().append(
            '<option disabled selected value="">Select Departure First</option>'
        );

        $("#updateStopsContainer").empty();
        $("#updateAddStopBtn").click();
    });



    $("#departureStationSelection").on("change", function () {
        const selectedTrain = $("#trainSelection").val();
        const selectedDepartureStation = $(this).val();

        loadStationForSelectionUsingTrain(
            $("#arrivalStationSelection"),
            selectedTrain,
            selectedDepartureStation
        );
    });

    $("#updateDepartureStationSelection").on("change", function () {
        const selectedTrain = $("#updateTrainSelection").val();
        const selectedDepartureStation = $(this).val();

        loadStationForSelectionUsingTrain(
            $("#updateArrivalStationSelection"),
            selectedTrain,
            selectedDepartureStation
        );
    });


    $("#arrivalStationSelection").on("change", function () {

       if ($("#departureStationSelection").val() && $("#arrivalStationSelection").val()) {
            loadStationForSelectionUsingTrain(
                $(".stop-station"),
                $("#trainSelection").val(),
                $("#departureStationSelection").val(),
                $("#arrivalStationSelection").val()
            );
       }

    });


    function loadStationForSelectionUsingTrain(
        selectElement,
        selectedTrain = null,
        selectedDepartureStation = null,
        selectedArrivalStation = null,
        selectedStation = null,
        selectedIntermediates = []  
    ) {
        const requestOptions = { method: "GET", redirect: "follow" };

        fetchWithTokenRefresh(
            `http://localhost:8080/api/v1/raillankapro/train/get/all/stations/by/name?trainname=${selectedTrain}`,
            requestOptions
        )
            .then(({ result }) => {
                if (result.code != 200) {
                    toastr.error(result.message);
                    return;
                }

                if (result.code === 200) {
                    const data = result.data;
                    data.sort((a, b) => a.stationName.localeCompare(b.stationName));

                    selectElement.empty();
                    selectElement.append(
                        '<option disabled selected value="">Select a Station</option>'
                    );

                    data.forEach((station) => {
                        let isDisabled = false;
                        let disableText = "";

                        if (!station.status) {
                            isDisabled = true;
                            disableText = " - inactive";
                        }

                        if (selectedDepartureStation && station.stationName === selectedDepartureStation) {
                            isDisabled = true;
                            disableText = " - departure";
                        }

                        if (selectedArrivalStation && station.stationName === selectedArrivalStation) {
                            isDisabled = true;
                            disableText = " - arrival";
                        }

                        if (selectedIntermediates.includes(station.stationName)) {
                            isDisabled = true;
                            disableText = " - already selected";
                        }

                        let optionHtml = `<option value="${station.stationName}" 
                            ${isDisabled ? "disabled" : ""} 
                            ${selectedStation === station.stationName ? "selected" : ""}>
                            ${station.stationName} - (${station.stationCode})${disableText}
                        </option>`;
                        selectElement.append(optionHtml);
                    });
                }
            })
            .catch((error) => console.error(error));
    }


    function loadTrainsForSelection(selectElement, selectedTrain = null, fullDetails = true) {

        const requestOptions = {
            method: "GET",
            redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/train/get/all/trains", requestOptions)
        .then(({result}) => {
            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 200) {
                const data = result.data;
                data.sort((a, b) => a.trainName.localeCompare(b.trainName));

                selectElement.empty();
                selectElement.append('<option disabled selected value="">Select a Train</option>');

                data.forEach(train => {
                    let isDisabled = false;

                    if (!train.active) {
                        isDisabled = true;
                    }

                    if (!fullDetails) {
                        train.category = "";
                        train.trainType = "";
                    }

                    let optionText = train.trainName;
                    if (fullDetails) {
                        optionText += ` - (${train.category.toLowerCase()} - ${train.trainType})`;
                    }
                    if (!train.active) {
                        optionText += ' - inactive';
                    }

                    const optionHtml = `<option value="${train.trainName}" 
                                            ${isDisabled ? 'disabled' : ''} 
                                            ${selectedTrain === train.trainName ? 'selected' : ''}>
                                            ${optionText}
                                        </option>`;
                    selectElement.append(optionHtml);

                });
                
            }
        })

        .catch((error) => console.error(error));
    }



    $("#departureTime, #arrivalTime").on("change", function () {
        validateTimes();
    });


    $("#updateDepartureTime, #updateArrivalTime").on("change", function () {
        updateScheduleValidateTimes();
    });

    $(document).on("change", ".stop-item input[type='time']", function () {
        validateTimes();
    });

     $(document).on("change", "#updateStopsContainer .stop-item input[type='time']", function () {
        updateScheduleValidateTimes();
    });

    $(document).on("click", "#updateAddStopBtn", function () {
        const $stopsContainer = $("#updateStopsContainer");
        const stopCount = $stopsContainer.children().length + 1;

        const newStop = $(`
            <div class="stop-item flex flex-col p-4 bg-white rounded-lg border border-gray-200 shadow-sm">
                <div class="flex items-center justify-between w-full">
                    <div class="flex items-center flex-1 space-x-3">
                        <span class="stop-number w-8 h-8 flex items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-blue-700 text-white font-medium text-sm">
                            ${stopCount}
                        </span>
                        <div class="relative flex-1">
                            <select class="stop-station w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white">
                                <option value="" disabled selected>Select Station</option>
                            </select>
                            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                <i class="fas fa-chevron-down"></i>
                            </div>
                        </div>
                        <div class="time-selection flex space-x-2">
                            <div>
                                <label class="text-xs text-gray-500 block mb-1">Arrival</label>
                                <input type="time" class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                            </div>
                            <div>
                                <label class="text-xs text-gray-500 block mb-1">Departure</label>
                                <input type="time" class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                            </div>
                        </div>
                    </div>
                    <button type="button" class="remove-stop ml-4 w-10 h-10 flex items-center justify-center rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-colors">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <p class="error-msg text-xs text-red-500 mt-2 ml-1 hidden"></p>
            </div>
        `);

        $stopsContainer.append(newStop);

        const selectedTrain = $("#updateTrainSelection").val();
        const selectedDeparture = $("#updateDepartureStationSelection").val();
        const selectedArrival = $("#updateArrivalStationSelection").val();

        if (selectedTrain) {
            const selectedIntermediates = $("#updateStopsContainer .stop-station")
                .map(function () {
                    return $(this).val();
                })
                .get()
                .filter(Boolean);

            loadStationForSelectionUsingTrain(
                newStop.find(".stop-station"), 
                selectedTrain, 
                selectedDeparture, 
                selectedArrival, 
                null, 
                selectedIntermediates
            );
        }

        setTimeout(updateScheduleValidateTimes, 200); 
    });

  
    $("#addStopBtn").on("click", function () {
        const $stopsContainer = $("#stopsContainer");
        const stopCount = $stopsContainer.children().length + 1;

        const newStop = $(`
        <div class="stop-item flex flex-col p-4 bg-white rounded-lg border border-gray-200 shadow-sm">
            <div class="flex items-center justify-between w-full">
                <div class="flex items-center flex-1 space-x-3">
                    <span class="stop-number w-8 h-8 flex items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-blue-700 text-white font-medium text-sm">
                        ${stopCount}
                    </span>
                    <div class="relative flex-1">
                        <select class="stop-station w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white">
                            <option value="" disabled selected>Select Station</option>
                        </select>
                        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                            <i class="fas fa-chevron-down"></i>
                        </div>
                    </div>
                    <div class="time-selection flex space-x-2">
                        <div>
                            <label class="text-xs text-gray-500 block mb-1">Arrival</label>
                            <input type="time" class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                        </div>
                        <div>
                            <label class="text-xs text-gray-500 block mb-1">Departure</label>
                            <input type="time" class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                        </div>
                    </div>
                </div>
                <button type="button" class="remove-stop ml-4 w-10 h-10 flex items-center justify-center rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-colors">
                    <i class="fas fa-times"></i>
                </button>
            </div>

            <!-- ✅ Error msg ekata hoda thanaka dala -->
            <p class="error-msg text-xs text-red-500 mt-2 ml-1 hidden"></p>
        </div>
        `);


        $stopsContainer.append(newStop);

        
        const selectedTrain = $("#trainSelection").val();
        const selectedDeparture = $("#departureStationSelection").val();
        const selectedArrival = $("#arrivalStationSelection").val();

        if (selectedTrain) {
            const selectedIntermediates = $(".stop-station")
            .map(function () {
                return $(this).val();
            })
            .get()
            .filter(Boolean);

        loadStationForSelectionUsingTrain(
            newStop.find(".stop-station"), 
            selectedTrain, 
            selectedDeparture, 
            selectedArrival, 
            null, 
            selectedIntermediates
        );
        }
        
        setTimeout(validateTimes, 200); 

        
    });

    function validateTimes() {
        const mainDeparture = $("#departureTime").val();
        const mainArrival = $("#arrivalTime").val();

        let isValid = true;
        let prevDeparture = mainDeparture;

        if (mainDeparture && mainArrival && mainDeparture >= mainArrival) {
            $("#mainArrivalTimeError").removeClass("hidden").text("Main Arrival must be later than Departure");
            isValid = false;
        } else {
            $("#mainArrivalTimeError").addClass("hidden").text("");
        }

        $("#stopsContainer .stop-item").each(function (index) {
            const $error = $(this).find(".error-msg"); 
            $error.addClass("hidden").text("");

            const arrival = $(this).find(".time-selection input[type='time']").eq(0).val();
            const departure = $(this).find(".time-selection input[type='time']").eq(1).val();

            if (arrival && departure) {
                if (arrival > departure) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Arrival cannot be after Departure`);
                    isValid = false;
                } else if (arrival < mainDeparture) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Arrival cannot be before Main Departure`);
                    isValid = false;
                } else if (departure > mainArrival) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Departure cannot be after Main Arrival`);
                    isValid = false;
                } else if (arrival < prevDeparture) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Arrival cannot be before previous Departure`);
                    isValid = false;
                }
                prevDeparture = departure;
            }
        });

        return isValid;
    }


    function updateScheduleValidateTimes() {
        const mainDeparture = $("#updateDepartureTime").val();
        const mainArrival = $("#updateArrivalTime").val();

        let isValid = true;
        let prevDeparture = mainDeparture;

        if (mainDeparture && mainArrival && mainDeparture >= mainArrival) {
            $("#updateMainArrivalTimeError").removeClass("hidden").text("Main Arrival must be later than Departure");
            isValid = false;
        } else {
            $("#updateMainArrivalTimeError").addClass("hidden").text("");
        }

        $("#updateStopsContainer .stop-item").each(function (index) {
            const $error = $(this).find(".error-msg"); 
            $error.addClass("hidden").text("");

            const arrival = $(this).find(".time-selection input[type='time']").eq(0).val();
            const departure = $(this).find(".time-selection input[type='time']").eq(1).val();

            if (arrival && departure) {
                if (arrival > departure) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Arrival cannot be after Departure`);
                    isValid = false;
                } else if (arrival < mainDeparture) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Arrival cannot be before Main Departure`);
                    isValid = false;
                } else if (departure > mainArrival) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Departure cannot be after Main Arrival`);
                    isValid = false;
                } else if (arrival < prevDeparture) {
                    $error.removeClass("hidden").text(`Stop ${index + 1}: Arrival cannot be before previous Departure`);
                    isValid = false;
                }
                prevDeparture = departure;
            }
        });

        return isValid;
    }


    $(document).on("click", ".remove-stop", function () {
        $(this).closest(".stop-item").remove();
        $("#stopsContainer .stop-item").each(function (index) {
        $(this).find(".stop-number").text(index + 1);
        });
    });

    $("#closeModal, #cancelBtn").on("click", function () {
        $("#createScheduleModal").removeClass("active");
    });

    $("#scheduleSubmitForm").on("submit", function (e) {
        e.preventDefault();

        if (!validateTimes()) {
            return;
        }

        const train = $("#trainSelection").val();
        console.log(`train name: ${train}`);
        
        const departureStation = $("#departureStationSelection").val();
        const arrivalStation = $("#arrivalStationSelection").val();

        const mainDepartureTime = $("#departureTime").val();
        const mainArrivalTime = $("#arrivalTime").val();

        const description = $("textarea").val() || "N/A";

        const scheduleFrequency = $("input[name='frequency']:checked").val();

        const stops = [];
        $("#stopsContainer .stop-item").each(function (index, element) {
            const stationId = $(element).find(".stop-station").val();
            const arrivalTime = $(element).find("input[type='time']").eq(0).val();
            const departureTime = $(element).find("input[type='time']").eq(1).val();

            if (stationId) {
                stops.push({
                    stationId: stationId,
                    arrivalTime: arrivalTime,
                    departureTime: departureTime,
                    stopOrder: index + 1
                });
            }
        });

        const raw = JSON.stringify({
            "trainName": train,
            "departureStation": departureStation,
            "arrivalStation": arrivalStation,
            "mainDepartureTime": mainDepartureTime,
            "mainArrivalTime": mainArrivalTime,
            "description": description,
            "scheduleFrequency": scheduleFrequency, 
            "stops": stops
        });

        console.log(raw);

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        


        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/schedule/register",  {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: raw
                })
        .then(({response,result}) => {
            console.log(response);
            if (result.code != 201) {
                toastr.error(result.message);
                return;
            }

            if (result.code === 201) {
                resetScheduleForm();
                closeModal("createScheduleModal");
                toastr.success(result.data);
                fetchSchedule(currentPage);
               
            }
        })
        .catch((error) => console.error(error));

                
    });

    $("#schedulUpdateForm").on("submit", function (e) {
        e.preventDefault();

        if (!updateScheduleValidateTimes()) return;

        const scheduleId = $("#updateScheduleId").val(); 

        const train = $("#updateTrainSelection").val(); 
        const departureStation = $("#updateDepartureStationSelection").val();
        const arrivalStation = $("#updateArrivalStationSelection").val();

        const mainDepartureTime = $("#updateDepartureTime").val();
        const mainArrivalTime = $("#updateArrivalTime").val();

        const description = $("#updateDesctiption").val() || "N/A";

        const scheduleFrequency = $("#updateFrequency input[name='frequency']:checked").val();
        console.log(`scheduleFrequency: ${scheduleFrequency}`);
        

        const status = $("input[name='updateStatus']:checked").val() === "true";

        const stops = [];
        $("#updateStopsContainer .stop-item").each(function (index, element) {
            const stationId = $(element).find(".stop-station").val();
            const arrivalTime = $(element).find("input[type='time']").eq(0).val();
            const departureTime = $(element).find("input[type='time']").eq(1).val();

            if (stationId) {
                stops.push({
                    stationId: stationId,
                    arrivalTime: arrivalTime,
                    departureTime: departureTime,
                    stopOrder: index + 1
                });
            }
        });

        const raw = JSON.stringify({
            "scheduleId": scheduleId,
            "trainName": train,
            "departureStation": departureStation,
            "arrivalStation": arrivalStation,
            "mainDepartureTime": mainDepartureTime,
            "mainArrivalTime": mainArrivalTime,
            "description": description,
            "scheduleFrequency": scheduleFrequency,
            "status": status,
            "stops": stops
        });

        fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/schedule/update`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: raw
        })
        .then(({response,result}) => {
            if (result.code != 200) {
                toastr.error(result.message);
                return;
            }
            if (result.code === 200) {
                closeModal("updateScheduleModal");
                toastr.success(result.data);
                fetchSchedule(currentPage,currentKeyword); 
            }
        })
        .catch((error) => console.error(error));
    });

    

    function resetScheduleForm() {
    
        $("#trainSelection").val("");
        $("#departureStationSelection").val("");
        $("#arrivalStationSelection").val("");
        $("#departureTime").val("");
        $("#arrivalTime").val("");
        $("textarea").val("");
        
        $("input[name='frequency'][value='DAILY']").prop("checked", true);
        $("#mainArrivalTimeError").addClass("hidden")
        $(".error-msg").addClass("hidden")
        $("#stopsContainer").empty();
        $("#addStopBtn").click();
    }

    function fetchSchedule(page, keyword = null, status = null, train = null, frequency = null) {
        let url = "";
        if (status) {
            url = `http://localhost:8080/api/v1/raillankapro/schedule/filter/${page}/5?status=${status}`;
        }else if (train) {
            url = `http://localhost:8080/api/v1/raillankapro/schedule/filter/${page}/5?train=${train}`;
        }else if (frequency) {
            url = `http://localhost:8080/api/v1/raillankapro/schedule/filter/${page}/5?frequency=${frequency}`;
        }else if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/schedule/filter/${page}/5?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/schedule/getall/${page}/5`;
        }

        const requestOptions = {
            method: "GET",
            redirect: "follow"
        };

        fetchWithTokenRefresh(url, requestOptions)
        .then(({result}) => {
            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 200) {
                const schedules = result.data;
                totalPages = result.totalPages;
                

                $('#currentPage').text(result.startNumber);
                $('#totalPage').text(result.totalItems);
                $("#selectedLastRowData").text(result.endNumber);

                $("#scheduleTable tbody").empty();

                if (!scheduleTable || schedules.length === 0) {
                    $("#scheduleTable tbody").append(
                        `<tr><td colspan="7" class="px-6 py-4 text-center text-gray-500">
                            No Schedules found
                        </td></tr>`
                    );
                    return;
                }

                schedules.forEach((schedule) => {
                    const randomColor = getRandomColor();
                    let statusText = schedule.status ? "active" : "inactive";
                    let statusClass = schedule.status ? "status-active" : "status-inactive";

                    $("#scheduleTable tbody").append(
                        `
                        <tr class="schedule-card">
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="flex items-center">
                                    <div class="flex-shrink-0 h-10 w-10">
                                        <div
                                        class="h-10 w-10 rounded-full bg-${randomColor}-100 flex items-center justify-center"
                                        >
                                        <i class="fas fa-train text-${randomColor}-600"></i>
                                        </div>
                                    </div>
                                    <div class="ml-4">
                                        <div class="text-sm font-medium text-gray-900 mb-1">
                                        ${schedule.trainName} [<span class="text-gray-500 text-xs">${schedule.trainId}</span>]
                                        </div>
                                        <span class ="text-sm text-gray-500">ID:</span>
                                        <div class="text-sm text-gray-500 inline">${schedule.scheduleId}</div>
                                        <div class="text-xs text-gray-400 mt-1">
                                         <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-${randomColor}-100 text-${randomColor}-700">
                                            ${schedule.departureStation}
                                            </span>
                                            <i class="fas fa-arrow-right mx-2 text-gray-300"></i>
                                            <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-${randomColor}-100 text-${randomColor}-700">
                                            ${schedule.arrivalStation}
                                        </span>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm font-medium text-gray-900 time-display">
                                ${schedule.mainDepartureTime}
                                </div>
                                <div class="text-sm text-gray-500">${schedule.departureStation}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm font-medium text-gray-900 time-display">
                                ${schedule.mainArrivalTime}
                                </div>
                                <div class="text-sm text-gray-500">${schedule.arrivalStation}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm text-gray-900">${schedule.duration}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm text-gray-900">${formatEnumString(schedule.scheduleFrequency)}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <span
                                class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}"
                                >${statusText}</span
                                >
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                <div class="flex space-x-2">
                                <button
                                    id="viewDetailsBtn"
                                    class="text-blue-600 hover:text-blue-900 view-stops-btn"
                                    data-schedule-id="${schedule.scheduleId}"
                                >
                                    <i class="fas fas fa-eye"></i>
                                </button>
                                <button
                                    id="updateScheduleBtn"
                                    class="text-blue-600 hover:text-blue-900"
                                    data-schedule-id="${schedule.scheduleId}"
                                >
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button
                                    id="deleteScheduleBtn"
                                    class="text-red-600 hover:text-red-900"
                                    data-id="${schedule.scheduleId}"
                                >
                                    <i class="fas fa-trash"></i>
                                </button>
                                <button class="text-gray-600 hover:text-gray-900">
                                    <i class="fas ${schedule.status ? "fa-toggle-on" : "fa-toggle-off"}"></i>
                                </button>
                                </div>
                            </td>
                            </tr>
                        `
                    );

                });

            }
            updatePaginationControls(currentPage, totalPages);


        });

    }

    $(document).on("click", "#updateScheduleBtn", function () {
        openModal("updateScheduleModal");
        const scheduleId = $(this).data("schedule-id");

        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/schedule/get/by?scheduleid=${scheduleId}`, requestOptions)
        .then(({result}) => {
            console.log(result)

            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }
            if (result.code === 200) {
                const data = result.data;
                $("#updateScheduleId").val(data.scheduleId);
                loadTrainsForSelection($("#updateTrainSelection"),data.trainName);

                loadStationForSelectionUsingTrain(
                $("#updateDepartureStationSelection"),
                    data.trainName,
                    null, 
                    null, 
                    data.departureStation 
                );

                loadStationForSelectionUsingTrain(
                    $("#updateArrivalStationSelection"),
                    data.trainName,
                    data.departureStation, 
                    null,
                    data.arrivalStation 
                );


                const status = data.status ? "active" : "inactive";
                $("#activeRadio").prop("checked", false);
                $("#inactiveRadio").prop("checked", false);

                if (status === "active") {
                    $("#activeRadio").prop("checked", true);
                } else {
                    $("#inactiveRadio").prop("checked", true);
                }


                $("#updateDepartureTime").val(data.mainDepartureTime)
                $("#updateArrivalTime").val(data.mainArrivalTime)

                $(`input[name="frequency"][value="${data.scheduleFrequency}"]`).prop("checked", true);
                $("#updateDesctiption").val(data.description)

                if (data.stops && data.stops.length > 0) {
                    // 🧹 Clear old/default stops
                    $("#updateStopsContainer").empty();

                    data.stops.forEach((stop, index) => {
                        const stopNumber = index + 1;

                        const newStop = $(`
                            <div class="stop-item flex flex-col p-4 bg-white rounded-lg border border-gray-200 shadow-sm">
                                <div class="flex items-center justify-between w-full">
                                    <div class="flex items-center flex-1 space-x-3">
                                        <span class="stop-number w-8 h-8 flex items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-blue-700 text-white font-medium text-sm">
                                            ${stopNumber}
                                        </span>
                                        <div class="relative flex-1">
                                            <select class="stop-station w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white">
                                                <option value="" disabled>Select Station</option>
                                            </select>
                                            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                                <i class="fas fa-chevron-down"></i>
                                            </div>
                                        </div>
                                        <div class="time-selection flex space-x-2">
                                            <div>
                                                <label class="text-xs text-gray-500 block mb-1">Arrival</label>
                                                <input type="time" value="${stop.arrivalTime}" class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                                            </div>
                                            <div>
                                                <label class="text-xs text-gray-500 block mb-1">Departure</label>
                                                <input type="time" value="${stop.departureTime}" class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                                            </div>
                                        </div>
                                    </div>
                                    <button type="button" class="remove-stop ml-4 w-10 h-10 flex items-center justify-center rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-colors">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                                <p class="error-msg text-xs text-red-500 mt-2 ml-1 hidden"></p>
                            </div>
                        `);

                        $("#updateStopsContainer").append(newStop);

                        const selectedTrain = data.trainName;
                        const selectedDeparture = data.departureStation;
                        const selectedArrival = data.arrivalStation;
                        const selectedIntermediates = data.stops
                            .filter((s, i) => i !== index)
                            .map(s => s.stationId);

                        // ✅ Populate stations
                        loadStationForSelectionUsingTrain(
                            newStop.find(".stop-station"),
                            selectedTrain,
                            selectedDeparture,
                            selectedArrival,
                            stop.stationId,   // select this station as default
                            selectedIntermediates
                        );
                    })  
                }



            }
            

        })
        .catch((error) => console.error(error));
    });

    $(document).on("click", "#viewDetailsBtn", function () {
        const scheduleId = $(this).data("schedule-id");
        openModal("scheduleModal");

        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/schedule/get/by?scheduleid=${scheduleId}`, requestOptions)
        .then(({result}) => {
            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 200) {
                const schedule = result.data;

                const formattedTrainType = schedule.trainType.substring(0,1).toUpperCase() + 
                schedule.trainType.substring(1).toLowerCase();

                $("#viewTrainId").text(schedule.trainId);
                $("#viewTrainName").text(schedule.trainName);
                $("#viewTrainType").text(formattedTrainType);
                $("#viewDepartureStation").text(schedule.departureStation);
                $("#viewArrivalStation").text(schedule.arrivalStation);

                let timelineHtml = "";

                // Departure
                timelineHtml += `
                    <div class="flex items-center mb-4">
                        <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center mr-4 z-10">
                            <i class="fas fa-play text-blue-600"></i>
                        </div>
                        <div class="flex-1">
                            <p class="font-medium">Departure - ${schedule.departureStation}</p>
                            <p class="text-sm text-gray-600 time-display">${schedule.mainDepartureTime}</p>
                        </div>
                    </div>
                `;

                // Stops
                schedule.stops.forEach((stop, index) => {
                    timelineHtml += `
                        <div class="flex items-center mb-4">
                            <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center mr-4 z-10">
                                <span class="text-gray-600 font-medium">${index + 1}</span>
                            </div>
                            <div class="flex-1">
                                <p class="font-medium">${stop.stationId}</p>
                                <p class="text-sm text-gray-600 time-display">${stop.arrivalTime} - ${stop.departureTime}</p>
                            </div>
                        </div>
                    `;
                });

                // Arrival
                timelineHtml += `
                    <div class="flex items-center">
                        <div class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center mr-4 z-10">
                            <i class="fas fa-flag-checkered text-green-600"></i>
                        </div>
                        <div class="flex-1">
                            <p class="font-medium">Arrival - ${schedule.arrivalStation}</p>
                            <p class="text-sm text-gray-600 time-display">${schedule.mainArrivalTime}</p>
                        </div>
                    </div>
                `;

                $(".route-line").html(timelineHtml);

                $("#viewDuration").text(schedule.duration);

                $("#viewFrequency").text(formatEnumString(schedule.scheduleFrequency));
                
                if (schedule.status) {
                    $("#viewStatus")
                    .addClass("bg-green-100 text-green-800")
                    .removeClass("bg-red-100 text-red-800")
                    .text("Active")
                }else{
                    $("#viewStatus")
                    .removeClass("bg-green-100 text-green-800")
                    .addClass("bg-red-100 text-red-800")
                    .text("Inactive")

                }

                $("#viewDescription").text(schedule.description);

                fetchDistance(schedule.departureStation, schedule.arrivalStation)
                .then(distanceData => {
                    if (distanceData.success) {
                        $("#viewDistance").text(`${distanceData.distance} (${distanceData.duration})`);
                    } else {
                        $("#viewDistance").text("Distance not available");
                    }
                })
                .catch(err => {
                    console.error("Distance fetch error:", err);
                    $("#viewDistance").text("Error fetching distance");
                });


            }
        })
        .catch((error) => console.error(error));

    });

    $(document).on("click", "#deleteScheduleBtn", async function () {
        const id = $(this).data("id");

        const resultSwal = await Swal.fire({
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
        });

        if (!resultSwal.isConfirmed) return;

        try {
            const { result } = await fetchWithTokenRefresh(
                `http://localhost:8080/api/v1/raillankapro/schedule/delete?id=${id}`,
                { method: "PUT" }
            );

            if (result.code === 200) {
                fetchSchedule(currentPage,currentKeyword);
                toastr.success(result.data);
            } else {
                toastr.error("Something went wrong!");
            }
        } catch (error) {
            console.error(error);
            toastr.error("Network or server error while deleting schedule.");
        }
    });

    async function fetchDistance(origin, destination) {
    try {
        const response = await fetchWithTokenRefresh(
            `http://localhost:8080/api/v1/raillankapro/distance?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}`,
            { method: "GET" }
        );
        
        return response.result;
    } catch (error) {
        console.error("Distance API error:", error);
        throw error;
    }
    }

    function formatEnumString(enumStr) {
        let withSpaces = enumStr.replace(/_/g, " ");
        return withSpaces
            .toLowerCase()
            .replace(/\b\w/g, (c) => c.toUpperCase());
    }

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
            fetchSchedule(currentPage, currentKeyword);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchSchedule(currentPage, currentKeyword);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchSchedule(currentPage, currentKeyword);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchSchedule(currentPage, currentKeyword);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchSchedule(currentPage, currentKeyword);
    }
    });

    $(document).on("click", ".fa-toggle-on, .fa-toggle-off", async function () {
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

        const scheduleId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim(); 
        const url = `http://localhost:8080/api/v1/raillankapro/schedule/change/status/${scheduleId}/${newStatus}`;

        try {
            const { result } = await fetchWithTokenRefresh(url, { method: "PUT" });
            console.log(result);

            if (result.code === 200) {
                fetchSchedule(currentPage,currentKeyword);     

                if (result.data) {
                    toastr.success(result.message);
                } else {
                    toastr.warning(result.message);
                }
            }
        } catch (error) {
            console.error(error);
            toastr.error("Something went wrong while changing status.");
        }
    });

    $("#filterSchedule").on("input", function () {
        $("#statusFilter").val("");
        $("#trainFilter").val("");
        $("frequencyFilter").val("");
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchSchedule(currentPage, currentKeyword);
    });

    $("#trainFilter").on("change", function () {
        fetchSchedule(1, null, null, $(this).val());
        $("#filterSchedule").val("");
        $("#statusFilter").val("");
        $("#frequencyFilter").val("");

        
    });

    $("#statusFilter").on("change", function () {
        $("#filterSchedule").val("");
        $("#trainFilter").val("");
        $("#frequencyFilter").val("");
        const currentStatus= $(this).val();
        if(currentStatus === "") {
            fetchSchedule(currentPage)
            return;
        }
        currentPage = 1;
        fetchSchedule(currentPage, null , currentStatus);
    });

    $("#frequencyFilter").on("change", function () {
        const frequency = $(this).val();
        if (frequency === "") {
            fetchSchedule(currentPage);
            return;
        }
        fetchSchedule(1, null, null, null, frequency);
        $("#filterSchedule").val("");
        $("#statusFilter").val("");
        $("#trainFilter").val("");
    });

    function getRandomColor() {
        let index;
        do {
            index = Math.floor(Math.random() * iconColors.length);
        } while (index === lastColorIndex); 

        lastColorIndex = index;
        return iconColors[index];
    }

    function updateErroMsgReset(){
        $("#updateMainArrivalTimeError").addClass("hidden");
        $("#updateStopsContainer .stop-item").each(function (index) {
            const $error = $(this).find(".error-msg"); 
            $error.addClass("hidden");
        });
    }

});