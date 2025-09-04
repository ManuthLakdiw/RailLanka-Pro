$(document).ready(function () {

    toastr.options = {
          closeButton: true,
          progressBar: true,
          positionClass: "toast-bottom-right",
          timeOut: 2000,
    };

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
        }
    );

    $("#closeUpdateModal, #updateCancelBtn").on(
        "click",
        function () {
            updateModal.removeClass("active");
        }
    );


    $("#trainUpdateForm").on("submit" , function(e) {
        e.preventDefault();


        let valid = true;

        $(".stop-station.new-stop").each(function(index) {
            if (!$(this).val() || $(this).val().trim() === "") {
                valid = false;
                toastr.warning(`Kindly select a station for stop ${index + 1}.`);
            }
        });

        if ($("#updateTrainTypeContainer").is(":visible")) {
            let selectedValue = $("#updateTrainType").val();
            if (!selectedValue) {
                toastr.warning("Kindly select a train type before proceeding.");
                valid = false;

            }
        }

        // Passenger classes validation
        if (!$(".passenger-update-fields").hasClass("hidden")) {
            let checkedCount = $(".passenger-update-fields input[type='checkbox']:checked").length;
            if (checkedCount === 0) {
                toastr.warning("Kindly select at least one class before proceeding.");
                valid = false;

            }
        }

        // Goods validation
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

        // Special train type validation
        if (!$(".special-update-fields").hasClass("hidden")) {
            if (!$("#updateSpecialTrainTypeSelection").val()) {
                toastr.warning("Kindly select a special train type before proceeding.");
                valid = false;
            }
        }

        if (!valid) return;


        const trainId = $("#updateTrainId").val();
        const trainName = $("#updateTrainName").val();
        const statusValue = $("input[name='updateStatus']:checked").val();  
        const boolChecked = (statusValue === "true");
        let trainCategory = "";
        let trainType = "";
        let classes = "";
        let capacity = 0;
        let cargoType = "";
        let specialTrainType = "";
        let specialFeature = "";
        let selStations = [];


        if ($("#updateTrainCategory").val() === "PASSENGER") {
            trainCategory = $("#updateTrainCategory").val();
            trainType = $("#updateTrainType").val();
            let checkedValues = $(".passenger-update-fields input[type='checkbox']:checked")
                .map(function() { 
                    return this.value; 
                }).get();
            classes = checkedValues.join(", ");

            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);
        }else if ($("#updateTrainCategory").val() === "POST") {
            trainCategory = $("#updateTrainCategory").val();
            trainType = $("#updateTrainType").val();
            classes = "Mail"
            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);
            
        }else if ($("#updateTrainCategory").val() === "GOODS") {
            trainCategory = $("#updateTrainCategory").val();
            trainType = $("#updateTrainType").val();
            classes = "Cargo"
            cargoType = $("#updateGoodsSelection").val();
            capacity = $("#updateGoodsCapacity").val();
            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);

        }else if ($("#updateTrainCategory").val() === "SPECIAL") {
            trainCategory = $("#updateTrainCategory").val();
            trainType = $("#updateTrainCategory").val();
            classes = "VIP Luxury"
            specialTrainType =  $("#updateSpecialTrainTypeSelection").val();
            specialFeature = $("#updateSpecialFeatureTxt").val();
            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);
        }

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjE5OTAzMSwiZXhwIjoxMDc1NjE5OTAzMX0.zBaQS1OVhuLsdtndTJ-7RDNGNIpqYf4bzyeVREMGJMo");

        const raw = JSON.stringify({
        "trainId": trainId,    
        "trainName": trainName,
        "category": trainCategory,
        "trainType": trainType,
        "classes": classes,
        "stations": selStations,
        "specialFeatures": specialFeature,
        "specialTrainType": specialTrainType,
        "cargoType": cargoType,
        "capacity": capacity,
        "active": boolChecked

        });

        const requestOptions = {
        method: "PUT",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/train/update", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code === 409) {
                $("#updateTrainNameMsg").text(result.message).addClass("text-red-500")
                return;
            }

            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 200) {
                $("#updateTrainNameMsg").text("").removeClass("text-red-500")
                toastr.success(result.data)
                updateModal.removeClass("active");
                fetchTrains(currentPage)
                

            }
        })
        .catch((error) => console.error(error));

    })


    $(document).on("click", "#updateTrainBtn", function () {
        console.log("clicked");
        updateModal.addClass("active")
        const id = $(this).data("id");
        const category = $(this).data("category")

        const updateTrainTypeContainer = $("#updateTrainTypeContainer");

        console.log(id);
        console.log(category);
        
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

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjI5MjE2NCwiZXhwIjoxMDc1NjI5MjE2NH0.8-5nkvTIRsq_7q2zQ2pEOzCK-O4A-3IPjts1uRxrmjQ");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/train/get/train/by?id=${id}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code != 200) {
                toastr.error(result.message)
                return
            }
            
            if (result.code === 200) {
                const data = result.data;  
                $("#updateTrainId").val(data.trainId)
                $("#updateTrainName").val(data.trainName)
                $("#updateTrainCategory").val(data.category)
                $("#updateTrainType").val(data.trainType)
                $("#updateSpecialTrainTypeSelection").val(data.specialTrainType)
                $("#updateSpecialFeatureTxt").val(data.specialFeatures)
                $("#updateGoodsSelection").val(data.cargoType)
                $("#updateGoodsCapacity").val(data.capacity)
            
                if (data.active) {
                    $("#activeRadio").prop("checked", true);
                }else {
                    $("#inactiveRadio").prop("checked", true);
                }

                if (data.classes) {
                    const classesArray = data.classes.split(",");

                    $("#updateFirstClass, #updateSecondClass, #updateThirdClass").prop("checked", false);

                    classesArray.forEach(cls => {
                        cls = cls.trim(); 
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
            }

        })
        .catch((error) => console.error(error));


    });

    $(document).on("click", ".view-stops-btn", function () {
        const trainId = $(this).data("train-id");
        const stationCount = $(this).data("train-stop-station")
        const $trainRow = $(this).closest("tr");
        const trainName = $trainRow.find(".text-sm.font-medium").text();

        $("#trainTitle").text(`${trainName} (${trainId})`);
        $("#stationCount").text(`${stationCount} station(s)`)
        $stopsModal.addClass("active");

        $(".stops-list").empty();

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjI5MjE2NCwiZXhwIjoxMDc1NjI5MjE2NH0.8-5nkvTIRsq_7q2zQ2pEOzCK-O4A-3IPjts1uRxrmjQ");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/train/get/station/by/train?id=${trainId}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if(result.code === 200) {
                const data = result.data;
                data.forEach((stationName, index) => {
                    const isStart = index === 0;
                    const isEnd = index === data.length - 1;
                    const bgColor = isStart ? "bg-blue-50" : isEnd ? "bg-blue-50" : "bg-white";
                    const circleColor = isStart ? "bg-green-500" : isEnd ? "bg-purple-500" : "bg-blue-500";
                    const infoText = isStart ? "Starting Station" : isEnd ? "Final Destination" : "Intermediate Stop";

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
        })
        .catch((error) => console.error(error));      

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

    $("#trainRegistrationForm").on("submit", function (e) {
        e.preventDefault();

        let valid = true;

        $(".stop-station").each(function (index) {
            if (!$(this).val()) {
                valid = false;
                toastr.warning(`Kindly select a station for stop ${index + 1}.`);
            }
        });

        if ($("#trainTypeContainer").is(":visible")) {
            let selectedValue = $("#trainType").val();
            if (!selectedValue) {
                toastr.warning("Kindly select a train type before proceeding.");
                valid = false;

            }
        }

        // Passenger classes validation
        if (!$(".passenger-fields").hasClass("hidden")) {
            let checkedCount = $(".passenger-fields input[type='checkbox']:checked").length;
            if (checkedCount === 0) {
                toastr.warning("Kindly select at least one class before proceeding.");
                valid = false;

            }
        }

        // Goods validation
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

        // Special train type validation
        if (!$(".special-fields").hasClass("hidden")) {
            if (!$("#specialTrainTypeSelection").val()) {
                toastr.warning("Kindly select a special train type before proceeding.");
                valid = false;
            }
        }

        if (!valid) return;

        
        const trainName = $("#trainName").val();
        let trainCategory = "";
        let trainType = "";
        let classes = "";
        let capacity = 0;
        let cargoType = "";
        let specialTrainType = "";
        let specialFeature = "";
        let selStations = [];

        if ($("#trainCategory").val() === "PASSENGER") {
            trainCategory = $("#trainCategory").val();
            trainType = $("#trainType").val();
            let checkedValues = $(".passenger-fields input[type='checkbox']:checked")
                .map(function() { 
                    return this.value; 
                }).get();
            classes = checkedValues.join(", ");

            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);
        }else if ($("#trainCategory").val() === "POST") {
            trainCategory = $("#trainCategory").val();
            trainType = $("#trainType").val();
            classes = "Mail"
            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);
            
        }else if ($("#trainCategory").val() === "GOODS") {
            trainCategory = $("#trainCategory").val();
            trainType = $("#trainType").val();
            classes = "Cargo"
            cargoType = $("#goodsSelection").val();
            capacity = $("#goodsCapacity").val();
            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);

        }else if ($("#trainCategory").val() === "SPECIAL") {
            trainCategory = $("#trainCategory").val();
            trainType = $("#trainCategory").val();
            classes = "VIP Luxury"
            specialTrainType =  $("#specialTrainTypeSelection").val();
            specialFeature = $("#specialFeatureTxt").val();
            selStations = $(".stop-station")
            .map(function () { return $(this).val(); })
            .get()
            .filter(v => v);

        }

        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjE5OTAzMSwiZXhwIjoxMDc1NjE5OTAzMX0.zBaQS1OVhuLsdtndTJ-7RDNGNIpqYf4bzyeVREMGJMo");

        const raw = JSON.stringify({
        "trainName": trainName,
        "category": trainCategory,
        "trainType": trainType,
        "classes": classes,
        "stations": selStations,
        "specialFeatures": specialFeature,
        "specialTrainType": specialTrainType,
        "cargoType": cargoType,
        "capacity": capacity
        });

        const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/train/register", requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code === 409) {
                $("#trainNameMsg").text(result.message).addClass("text-red-500")
                return;
            }

            if (result.code != 201) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 201) {
                toastr.success(result.data)
                $trainModal.removeClass("active");
                fetchTrains(currentPage)
                resetRegisterFields();

            }
        })
        .catch((error) => console.error(error));
    });


    $(window).on("click", function (event) {
        if (event.target === $trainModal[0]) {
        $trainModal.removeClass("active");
        }
        if (event.target === $stopsModal[0]) {
        $stopsModal.removeClass("active");
        }
    });


    


    function loadStationsForSelection(selectElement, selectedStation = null) {

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjIyOTYyOSwiZXhwIjoxMDc1NjIyOTYyOX0.zJwj3bRtMjOV347ayHfkITMA1KFWfXJyrns1-0DSw58");

        const requestOptions = {
            method: "GET",
            headers: myHeaders,
            redirect: "follow"
        };

        fetch("http://localhost:8080/api/v1/raillankapro/station/getall/names/and/codes", requestOptions)
        .then((response) => response.json())
        .then((result) => {

            if (result.code != 200) {
                toastr.error(result.message);
                return;
            }

            const allStations = result.data;
            allStations.sort((a, b) => a.name.localeCompare(b.name));

            selectElement.empty();
            selectElement.append('<option disabled selected value="">Select a Station</option>');

            allStations.forEach(station => {
            let isDisabled = false;

            if (!station.inService) {
                isDisabled = true;
            }

            if (selectedStations.includes(station.name) && station.name !== selectedStation) {
                isDisabled = true;
            }

            let optionHtml = `<option value="${station.name}" ${isDisabled ? 'disabled' : ''} ${selectedStation === station.name ? 'selected' : ''}>
                                ${station.name} (${station.stationCode})${!station.inService ? ' - out-of-service' : ''}
                            </option>`;
            selectElement.append(optionHtml);
        }   );
        })
        .catch((error) => console.error(error));
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
        fetchTrains(currentPage, "" , category);

    })

    function fetchTrains(page, keyword = "" , category = "") {
        let url = "";
        if (category) {
             url = `http://localhost:8080/api/v1/raillankapro/train/filter/by/category/${page}/7?category=${category}`;
        }else if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/train/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/train/getall/${page}/7`;
        }


        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjI5MjE2NCwiZXhwIjoxMDc1NjI5MjE2NH0.8-5nkvTIRsq_7q2zQ2pEOzCK-O4A-3IPjts1uRxrmjQ");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(url, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

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
                let icon = "";
                if(trainCategory === "PASSENGER") {
                    randomColor = "blue"
                    icon = "users"
                }else if (trainCategory === "POST") {
                    randomColor = "yellow"
                    icon = "envelope"
                }else if (trainCategory === "GOODS") {
                    randomColor = "purple"
                    icon = "boxes"
                }else if (trainCategory === "SPECIAL") {
                    randomColor = "pink"
                    icon = "star"
                }

                let classBadges = "";
                if (train.classes) {
                    classBadges = train.classes.split(",").map(c =>
                        `<span class="class-badge">${c.trim()}</span>`
                    ).join(" ");
                }

                 $("#trainTable tbody").append(
                    `                    
                    <tr class="hover:bg-gray-50" data-category="passenger">
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                            <div
                                class="flex-shrink-0 h-10 w-10 rounded-full bg-${randomColor}-100 flex items-center justify-center"
                            >
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
                            <span
                            class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}"
                            >
                            ${statusText}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            <div class="flex space-x-2">
                            <button
                                class="text-blue-600 hover:text-blue-900 view-stops-btn"
                                data-train-id="${train.trainId}"
                                data-train-stop-station="${train.stopStationCount}"
                            >
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
                    `
                );

            });
            updatePaginationControls(page, totalPages);


        })
        .catch((error) => console.error(error));

    }

    $("#filterTrain").on("input", function () {
        $("#categoryFilter").val("all");
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchTrains(currentPage, currentKeyword,"");
    });

    $(document).on("click", "#deleteTrainBtn", function () {
        
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

                fetch(`http://localhost:8080/api/v1/raillankapro/train/delete?id=${id}`, requestOptions)
                .then((response) => response.json())
                .then((result) => {

                    if (result.code != 200) {
                        toastr.warning(result.message)
                        return;
                    }

                    if (result.code === 200) {
                        fetchTrains(currentPage)
                        loadStationsForSelection();
                        Swal.fire({
                            title: "Deleted!",
                            text: result.data,
                            icon: "success",
                            showConfirmButton: false,   
                            timer: 1300  
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

        const trainId = row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim();

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NjMxMzQzOSwiZXhwIjoxMDc1NjMxMzQzOX0.aex56HjdPmxb5xDcJapvKbfw_DCUKDN3eQzlpFDnbt0");

        const requestOptions = {
        method: "PUT",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/train/changestatus/${trainId}/${newStatus}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code != 200) {
                toastr.warning(result.message)
                return;
            }
            if (result.code === 200) {
                loadStationsForSelection();
                fetchTrains(currentPage);
                $("#filterTrain").val("");
                if (result.data) {
                    toastr.success(result.message);
                }else {
                    toastr.warning(result.message);  
                }

            }

        })
        .catch((error) => console.error(error));

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
            fetchTrains(currentPage, currentKeyword);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchTrains(currentPage, currentKeyword);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchTrains(currentPage, currentKeyword);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchTrains(currentPage, currentKeyword);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchTrains(currentPage, currentKeyword);
    }
    });


});
