$(document).ready(function () {
    toastr.options = {
          closeButton: true,
          progressBar: true,
          positionClass: "toast-top-right",
          timeOut: 2000,
    };

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


    let selectedStations = [];
    let currentPage = 1;
    let totalPages = 0;
    const maxVisiblePages = 5;
    let lastColorIndex = -1; 
    let currentKeyword = "";
    let category = "all"

    const $updateStopsContainer = $("#updateStopsContainer");

    fetchTrains(currentPage);


    const $trainModal = $("#trainModal");
    const $stopsModal = $("#stopsModal");
    const updateModal = $("#trainUpdateModal");

    $("#addTrainBtn").on("click", function () {
        $trainModal.addClass("active");
        const firstSelect = $trainModal.find(".stop-station").first();
        selectedStations = [];
        loadStationsForSelection(firstSelect);
        fetchTrains(currentPage);

    });

    $("#closeModal, #closeStopsModal, #cancelBtn, #closeStopsModalBtn").on(
        "click",
        function () {
            $trainModal.removeClass("active");
            $stopsModal.removeClass("active");
            resetRegisterFields();
            selectedStations = [];

        }
    );

    $("#closeUpdateModal, #updateCancelBtn").on(
        "click",
        function () {
            updateModal.removeClass("active");
            selectedStations = [];

        }
    );


    $("#trainUpdateForm").on("submit", async function(e) {
        e.preventDefault();

        let valid = true;

        $(".stop-station.new-stop").each(function(index) {
            if (!$(this).val() || $(this).val().trim() === "") {
                valid = false;
                toastr.warning(`Kindly select a station for stop ${index + 1}.`);
            }
        });

        if ($("#updateTrainTypeContainer").is(":visible") && !$("#updateTrainType").val()) {
            toastr.warning("Kindly select a train type before proceeding.");
            valid = false;
        }

        if (!$(".passenger-update-fields").hasClass("hidden") &&
            $(".passenger-update-fields input[type='checkbox']:checked").length === 0) {
            toastr.warning("Kindly select at least one class before proceeding.");
            valid = false;
        }

        if (!$(".goods-update-fields").hasClass("hidden")) {
            if (!$("#updateGoodsSelection").val()) {
                toastr.warning("Kindly select at least one cargo type before proceeding.");
                valid = false;
            }
            if (!$("#updateGoodsCapacity").val()) {
                toastr.warning("Kindly select a goods capacity before proceeding.");
                valid = false;
            }
        }

        if (!$(".special-update-fields").hasClass("hidden") && !$("#updateSpecialTrainTypeSelection").val()) {
            toastr.warning("Kindly select a special train type before proceeding.");
            valid = false;
        }

        if (!valid) return;

        const trainId = $("#updateTrainId").val();
        const trainName = $("#updateTrainName").val();
        const boolChecked = $("input[name='updateStatus']:checked").val() === "true";

        let trainCategory = $("#updateTrainCategory").val();
        let trainType = "";
        let classes = "";
        let capacity = 0;
        let cargoType = "";
        let specialTrainType = "";
        let specialFeature = "";
        let selStations = $(".stop-station").map(function() { return $(this).val(); }).get().filter(v => v);

        if (trainCategory === "PASSENGER") {
            trainType = $("#updateTrainType").val();
            classes = $(".passenger-update-fields input[type='checkbox']:checked").map(function() { return this.value; }).get().join(", ");
        } else if (trainCategory === "POST") {
            trainType = $("#updateTrainType").val();
            classes = "Mail";
        } else if (trainCategory === "GOODS") {
            trainType = $("#updateTrainType").val();
            classes = "Cargo";
            cargoType = $("#updateGoodsSelection").val();
            capacity = $("#updateGoodsCapacity").val();
        } else if (trainCategory === "SPECIAL") {
            trainType = trainCategory;
            classes = "VIP Luxury";
            specialTrainType = $("#updateSpecialTrainTypeSelection").val();
            specialFeature = $("#updateSpecialFeatureTxt").val();
        }

        try {
            const { response, result } = await fetchWithTokenRefresh(
                "http://localhost:8080/api/v1/raillankapro/train/update",
                {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        trainId,
                        trainName,
                        category: trainCategory,
                        trainType,
                        classes,
                        stations: selStations,
                        specialFeatures: specialFeature,
                        specialTrainType,
                        cargoType,
                        capacity,
                        active: boolChecked
                    })
                }
            );

            if (!response || !result) return;

            if (result.code === 409) {
                $("#updateTrainNameMsg").text(result.message).addClass("text-red-500");
                return;
            }

            if (result.code !== 200) {
                toastr.error(result.message);
                return;
            }

            $("#updateTrainNameMsg").text("").removeClass("text-red-500");
            toastr.success(result.data);
            updateModal.removeClass("active");
            fetchTrains(currentPage);

        } catch (error) {
            console.error(error);
        }
    });



    $(document).on("click", "#updateTrainBtn", async function () {
        updateModal.addClass("active");

        const id = $(this).data("id");
        const category = $(this).data("category");

        const updateTrainTypeContainer = $("#updateTrainTypeContainer");

        if (category === "SPECIAL") {
            updateTrainTypeContainer.hide();
        } else {
            updateTrainTypeContainer.show();
        }

        $(".passenger-update-fields, .goods-update-fields, .special-update-fields").addClass("hidden");

        if (category === "PASSENGER") $(".passenger-update-fields").removeClass("hidden");
        else if (category === "GOODS") $(".goods-update-fields").removeClass("hidden");
        else if (category === "SPECIAL") $(".special-update-fields").removeClass("hidden");

        try {
            const { response, result } = await fetchWithTokenRefresh(
                `http://localhost:8080/api/v1/raillankapro/train/get/train/by?id=${id}`,
                { method: "GET" }
            );

            if (!response || !result) return;

            if (result.code !== 200) {
                toastr.error(result.message);
                return;
            }

            const data = result.data;

            $("#updateTrainId").val(data.trainId);
            $("#updateTrainName").val(data.trainName);
            $("#updateTrainCategory").val(data.category);
            $("#updateTrainType").val(data.trainType);
            $("#updateSpecialTrainTypeSelection").val(data.specialTrainType);
            $("#updateSpecialFeatureTxt").val(data.specialFeatures);
            $("#updateGoodsSelection").val(data.cargoType);
            $("#updateGoodsCapacity").val(data.capacity);

            if (data.active) $("#activeRadio").prop("checked", true);
            else $("#inactiveRadio").prop("checked", true);

            if (data.classes) {
                const classesArray = data.classes.split(",").map(cls => cls.trim());
                $("#updateFirstClass, #updateSecondClass, #updateThirdClass").prop("checked", false);

                classesArray.forEach(cls => {
                    if (cls === "1st") $("#updateFirstClass").prop("checked", true);
                    if (cls === "2nd") $("#updateSecondClass").prop("checked", true);
                    if (cls === "3rd") $("#updateThirdClass").prop("checked", true);
                });
            }

            if (data.stations && data.stations.length > 0) {
                $updateStopsContainer.empty();
                selectedStations = [];

                data.stations.forEach(station => {
                    selectedStations.push(station);
                    addStopItem(station);
                });
            }

        } catch (error) {
            console.error(error);
        }
    });


    $(document).on("click", ".view-stops-btn", async function () {
        const trainId = $(this).data("train-id");
        const stationCount = $(this).data("train-stop-station");
        const $trainRow = $(this).closest("tr");
        const trainName = $trainRow.find(".text-sm.font-medium").text();

        $("#trainTitle").text(`${trainName} (${trainId})`);
        $("#stationCount").text(`${stationCount} station(s)`);
        $stopsModal.addClass("active");

        $(".stops-list").empty();

        try {
            const { response, result } = await fetchWithTokenRefresh(
                `http://localhost:8080/api/v1/raillankapro/train/get/station/by/train?id=${trainId}`,
                { method: "GET" }
            );

            if (!response || !result) return;

            if (result.code !== 200) {
                toastr.error(result.message);
                return;
            }

            if (result.code === 200) {
                const data = result.data;

                data.forEach((stationName, index) => {
                    const isStart = index === 0;
                    const isEnd = index === data.length - 1;
                    const bgColor = isStart ? "bg-blue-50" : isEnd ? "bg-blue-50" : "bg-white";
                    const circleColor = isStart ? "bg-green-500" : isEnd ? "bg-purple-500" : "bg-blue-500";
                    const infoText = isStart ? "First Route Station" : isEnd ? "Last Route Station" : "Intermediate Route";

                    const stationHtml = `
                        <div class="stop-item flex items-center p-3 ${bgColor} rounded-lg">
                            <div class="w-8 h-8 rounded-full ${circleColor} text-white flex items-center justify-center font-bold mr-4 shadow-md relative z-10">
                                ${index + 1}
                            </div>
                            <div class="flex-1">
                                <span class="font-medium text-gray-800">${stationName}</span>
                                <p class="text-xs text-gray-500 mt-1">${infoText}</p>
                            </div>
                        </div>
                    `;
                    $(".stops-list").append(stationHtml);
                });
            }

        } catch (error) {
            console.error(error);
        }
    });


    

    $("#updateAddStopBtn").on("click", function () {
        addStopItem();
    });

    function addStopItem(selectedStationId = null) {
    const stopCount = $updateStopsContainer.find(".stop-item").length + 1;

    const newStop = $(`
        <div class="stop-item flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-200">
            <div class="flex items-center flex-1">
                <span class="stop-number w-8 h-8 flex items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-blue-700 text-white font-medium text-sm mr-4">
                    ${stopCount}
                </span>
                <div class="relative flex-1">
                    <select class="stop-station new-stop w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white">
                        <option value="" disabled selected>Select Station</option>
                    </select>
                    <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                        <i class="fas fa-chevron-down"></i>
                    </div>
                </div>
            </div>
            <button type="button" class="remove-stop ml-4 w-10 h-10 flex items-center justify-center rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-colors">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `);

    $updateStopsContainer.append(newStop);

    loadStationsForSelection(newStop.find(".stop-station"), selectedStationId);
}


    function reorderStopNumbers() {
        $updateStopsContainer.find(".stop-item").each(function (index) {
            $(this).find(".stop-number").text(index + 1);
    });
}


    const $stopsContainer = $("#stopsContainer");
    $("#addStopBtn").on("click", function () {
        const stopCount = $stopsContainer.find(".stop-item").length + 1;

        const newStop = $(`
        <div class="stop-item flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-200">
            <div class="flex items-center flex-1">
            <span class="stop-number w-8 h-8 flex items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-blue-700 text-white font-medium text-sm mr-4">
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
            </div>
            <button type="button" class="remove-stop ml-4 w-10 h-10 flex items-center justify-center rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-colors">
            <i class="fas fa-times"></i>
            </button>
        </div>
        `);

        $stopsContainer.append(newStop);

        // New select element එකට stations load කරන්න
        loadStationsForSelection(newStop.find(".stop-station"));
    });

    $updateStopsContainer.on("click", ".remove-stop", function () {
    const totalStops = $updateStopsContainer.find(".stop-item").length;

        if (totalStops > 1) {
            $(this).closest(".stop-item").remove();
            reorderStopNumbers();
        } else {
            toastr.warning("A train must have at least one stop.");
        }
    });

    $stopsContainer.on("click", ".remove-stop", function () {
        if ($stopsContainer.find(".stop-item").length > 1) {
        $(this).closest(".stop-item").remove();
        $stopsContainer.find(".stop-item").each(function (index) {
            $(this).find(".stop-number").text(index + 1);
        });
        } else {
        toastr.warning("A train must have at least one stop.")
        }
    });

  $("#trainCategory").on("change", function () {
    const category = $(this).val();
    const $trainTypeContainer = $("#trainTypeContainer");

    if (category === "SPECIAL") {
      $trainTypeContainer.hide();
    } else {
      $trainTypeContainer.show();
    }

    $(".passenger-fields, .goods-fields, .special-fields").addClass("hidden");

    if (category === "PASSENGER") {
      $(".passenger-fields").removeClass("hidden"); 
    } else if (category === "GOODS") {
      $(".goods-fields").removeClass("hidden");
    } else if (category === "SPECIAL") {
      $(".special-fields").removeClass("hidden");
    }
    });

    $("#updateTrainCategory").on("change", function () {
        const category = $(this).val();
        const updateTrainTypeContainer = $("#updateTrainTypeContainer");

        if (category === "SPECIAL") {
            updateTrainTypeContainer.hide();
        } else {
            updateTrainTypeContainer.show();
        }

        $(".passenger-update-fields, .goods-update-fields, .special-update-fields").addClass("hidden");

        if (category === "PASSENGER") {
        $(".passenger-update-fields").removeClass("hidden"); 
        } else if (category === "GOODS") {
        $(".goods-update-fields").removeClass("hidden");
        } else if (category === "SPECIAL") {
        $(".special-update-fields").removeClass("hidden");
        }
    });

    $("#trainRegistrationForm").on("submit", async function (e) {
        e.preventDefault();

        let valid = true;

        $(".stop-station").each(function (index) {
            if (!$(this).val()) {
                valid = false;
                toastr.warning(`Kindly select a station for stop ${index + 1}.`);
            }
        });

        if ($("#trainTypeContainer").is(":visible") && !$("#trainType").val()) {
            toastr.warning("Kindly select a train type before proceeding.");
            valid = false;
        }

        if (!$(".passenger-fields").hasClass("hidden") && $(".passenger-fields input[type='checkbox']:checked").length === 0) {
            toastr.warning("Kindly select at least one class before proceeding.");
            valid = false;
        }

        if (!$(".goods-fields").hasClass("hidden")) {
            if (!$("#goodsSelection").val()) {
                toastr.warning("Kindly select at least one cargo type before proceeding.");
                valid = false;
            }
            if (!$("#goodsCapacity").val()) {
                toastr.warning("Kindly select a goods capacity before proceeding.");
                valid = false;
            }
        }

        if (!$(".special-fields").hasClass("hidden") && !$("#specialTrainTypeSelection").val()) {
            toastr.warning("Kindly select a special train type before proceeding.");
            valid = false;
        }

        if (!valid) return;

        const trainName = $("#trainName").val();
        const trainCategory = $("#trainCategory").val();
        const trainType = trainCategory === "SPECIAL" ? trainCategory : $("#trainType").val();
        let classes = "";
        let capacity = 0;
        let cargoType = "";
        let specialTrainType = "";
        let specialFeature = "";
        const selStations = $(".stop-station").map(function() { return $(this).val(); }).get().filter(v => v);

        if (trainCategory === "PASSENGER") {
            classes = $(".passenger-fields input[type='checkbox']:checked").map(function() { return this.value; }).get().join(", ");
        } else if (trainCategory === "POST") {
            classes = "Mail";
        } else if (trainCategory === "GOODS") {
            classes = "Cargo";
            cargoType = $("#goodsSelection").val();
            capacity = $("#goodsCapacity").val();
        } else if (trainCategory === "SPECIAL") {
            classes = "VIP Luxury";
            specialTrainType = $("#specialTrainTypeSelection").val();
            specialFeature = $("#specialFeatureTxt").val();
        }

        const raw = JSON.stringify({
            trainName,
            category: trainCategory,
            trainType,
            classes,
            stations: selStations,
            specialFeatures: specialFeature,
            specialTrainType,
            cargoType,
            capacity
        });

        try {
            const { response, result } = await fetchWithTokenRefresh(
                "http://localhost:8080/api/v1/raillankapro/train/register",
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: raw,
                    redirect: "follow"
                }
            );

            if (!response || !result) return;

            if (result.code === 409) {
                $("#trainNameMsg").text(result.message).addClass("text-red-500");
                return;
            }

            if (result.code !== 201) {
                toastr.error(result.message);
                return;
            }

            toastr.success(result.data);
            $trainModal.removeClass("active");
            fetchTrains(currentPage);
            resetRegisterFields();

        } catch (error) {
            console.error(error);
        }
    });





    $(window).on("click", function (event) {
        if (event.target === $trainModal[0]) {
        $trainModal.removeClass("active");
        selectedStations = [];

        }
        if (event.target === $stopsModal[0]) {
        $stopsModal.removeClass("active");
        selectedStations = [];

        }
    });


    


    async function loadStationsForSelection(selectElement, selectedStation = null) {
        try {
            const { response, result } = await fetchWithTokenRefresh(
                "http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes",
                {
                    method: "GET",
                    redirect: "follow"
                }
            );

            if (!response || !result) return;

            if (result.code !== 200) {
                toastr.error(result.message);
                return;
            }

            const allStations = result.data;
            allStations.sort((a, b) => a.name.localeCompare(b.name));

            selectElement.empty();
            selectElement.append('<option disabled selected value="">Select a Station</option>');

            allStations.forEach(station => {
                let isDisabled = false;

                if (!station.inService) isDisabled = true;

                if (selectedStations.includes(station.name) && station.name !== selectedStation) {
                    isDisabled = true;
                }

                let optionHtml = `<option value="${station.name}" ${isDisabled ? 'disabled' : ''} ${selectedStation === station.name ? 'selected' : ''}>
                                    ${station.name} (${station.stationCode})${!station.inService ? ' - out-of-service' : ''}
                                </option>`;
                selectElement.append(optionHtml);
            });

        } catch (error) {
            console.error(error);
        }
    }


    $(document).on("change", ".stop-station", function() {
       
        selectedStations = $(".stop-station")
        .map(function() { 
            return $(this).val(); 
        })
        .get()
        .filter(v => v);
    });

    function resetRegisterFields() {

        $("#trainName").val("")
        $("#trainCategory").val("")
        $("#trainType").val("");
        $(".passenger-fields input[type='checkbox']").prop("checked", false);
        $("#specialTrainTypeSelection").val("");
        $("#specialFeatureTxt").val("")
        $("#goodsSelection").val("")
        $("#goodsCapacity").val("")
        $("#trainNameMsg").text("").removeClass("text-red-500")

        const $stopsContainer = $("#stopsContainer");

        // keep only first stop-item, remove others
        $stopsContainer.find(".stop-item").not(":first").remove();

        // reset first stop's select value
        $stopsContainer.find(".stop-item:first .stop-station").val("");

        // stop number reset to 1
        $stopsContainer.find(".stop-item:first .stop-number").text("1");


        $(".passenger-fields").removeClass("hidden")
        $(".goods-fields").addClass("hidden")
        $(".special-fields").addClass("hidden")
        $("#trainTypeContainer").show();
        
        selectedStations = [];
    }

    $("#categoryFilter").on("change" , function () {
        const currentCategory= $("#categoryFilter").val();
        if(currentCategory === "all") {
            fetchTrains(currentPage)
            return;
        }
        category = currentCategory; 
        currentPage = 1;
        fetchTrains(currentPage, currentKeyword , category);

    })
    async function fetchTrains(page, keyword = "", category = "") {
        let url = "";
        if (category && category !== "all") {
            url = `http://localhost:8080/api/v1/raillankapro/train/filter/by/category/${page}/7?category=${category}`;
        } else if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/train/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/train/getall/${page}/7`;
        }

        try {
            const { response, result } = await fetchWithTokenRefresh(url, {
                method: "GET",
                redirect: "follow"
            });

            if (!response || !result) {
                return;
            }

            console.log(result);

            const trains = result.data;
            totalPages = result.totalPages;

            $('#currentPage').text(result.startNumber);
            $('#totalPage').text(result.totalItems);
            $("#selectedLastRowData").text(result.endNumber);

            $("#trainTable tbody").empty();

            if (trains.length === 0) {
                $("#trainTable tbody").append(
                `<tr><td colspan="7" class="px-6 py-4 text-center text-gray-500">
                    No Trains found
                </td></tr>`
                );
                return;
            }

            trains.forEach(train => {
                let statusText = train.active ? "active" : "inactive";
                let statusClass = train.active ? "status-active" : "status-inactive";
                let formatedCategory = train.category.charAt(0).toUpperCase() + train.category.slice(1).toLowerCase();
                let formatedTrainType = train.trainType.charAt(0).toUpperCase() + train.trainType.slice(1).toLowerCase();

                let trainCategory = train.category;
                let randomColor = ""; // Fixed: declare randomColor
                let icon = "";
                if(trainCategory === "PASSENGER") {
                    randomColor = "blue"
                    icon = "users"
                } else if (trainCategory === "POST") {
                    randomColor = "yellow"
                    icon = "envelope"
                } else if (trainCategory === "GOODS") {
                    randomColor = "purple"
                    icon = "boxes"
                } else if (trainCategory === "SPECIAL") {
                    randomColor = "pink"
                    icon = "star"
                }

                let classBadges = "";
                if (train.classes) {
                    classBadges = train.classes.split(",").map(c =>
                        `<span class="class-badge">${c.trim()}</span>`
                    ).join(" ");
                }

                $("#trainTable tbody").append(`
                    <tr class="hover:bg-gray-50" data-category="passenger">
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                            <div class="flex-shrink-0 h-10 w-10 rounded-full bg-${randomColor}-100 flex items-center justify-center">
                                <i class="fas fa-${icon} text-${randomColor}-600"></i>
                            </div>
                            <div class="ml-4">
                                <div class="text-sm font-medium text-gray-900">
                                ${train.trainName}
                                </div>
                                <span class ="text-sm text-gray-500">ID:</span>
                                <div class="text-sm text-gray-500 inline">${train.trainId}</div>
                            </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <span class="category-passenger">${formatedCategory}</span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <span class="station-badge">${formatedTrainType}</span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                        ${classBadges}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            ${train.stopStationCount} station(s)
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}">
                            ${statusText}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            <div class="flex space-x-2">
                            <button class="text-blue-600 hover:text-blue-900 view-stops-btn" data-train-id="${train.trainId}" data-train-stop-station="${train.stopStationCount}">
                                <i class="fas fa-map-marker-alt"></i>
                            </button>
                            <button id="updateTrainBtn" class="text-blue-600 hover:text-blue-900" data-id="${train.trainId}" data-category="${train.category}"> 
                                <i class="fas fa-edit"></i>
                            </button>
                            <button id="deleteTrainBtn" class="text-red-600 hover:text-red-900" data-id="${train.trainId}">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="text-gray-600 hover:text-gray-900">
                                <i class="fas ${train.active ? "fa-toggle-on" : "fa-toggle-off"}"></i>
                            </button>
                            </div>
                        </td>
                    </tr>
                `);
            });
            
            updatePaginationControls(page, totalPages);

        } catch (error) {
            console.log("Train fetch error:", error);
            $("#trainTable tbody").html(
                '<tr><td colspan="7" class="px-6 py-4 text-center text-red-500">Error loading trains</td></tr>'
            );
        }
}

    $("#filterTrain").on("input", function () {
        $("#categoryFilter").val("all");
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchTrains(currentPage, currentKeyword,"");
    });

    $(document).on("click", "#deleteTrainBtn", async function () {
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
            const { response, result } = await fetchWithTokenRefresh(
                `http://localhost:8080/api/v1/raillankapro/train/delete?id=${id}`,
                { method: "PUT", redirect: "follow" }
            );

            if (!response || !result) return;

            if (result.code !== 200) {
                toastr.warning(result.message);
                return;
            }

            fetchTrains(currentPage);
            loadStationsForSelection();

            toastr.success(result.data);
        } catch (error) {
            console.error(error);
            toastr.error("Something went wrong while deleting.");            
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

        const trainId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim();

        try {
            const { response, result } = await fetchWithTokenRefresh(
                `http://localhost:8080/api/v1/raillankapro/train/changestatus/${trainId}/${newStatus}`,
                { method: "PUT", redirect: "follow" }
            );

            if (!response || !result) return;

            fetchTrains(currentPage,currentKeyword,category);

            if (result.data) {
                toastr.success(result.message);
            } else {
                toastr.warning(result.message);
            }

        } catch (error) {
            console.error(error);
            fetchTrains(currentPage,currentKeyword);
        }

        
    });





    function getRandomColor() {
        let index;
        do {
            index = Math.floor(Math.random() * iconColors.length);
        } while (index === lastColorIndex); 

        lastColorIndex = index;
        return iconColors[index];
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
            fetchTrains(currentPage, currentKeyword, category);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchTrains(currentPage, currentKeyword, category);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchTrains(currentPage, currentKeyword, category);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchTrains(currentPage, currentKeyword, category);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchTrains(currentPage, currentKeyword, category);
    }
    });


});
