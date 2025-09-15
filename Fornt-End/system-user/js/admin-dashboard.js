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

$('#logoutButton').on('click', function(e) {
    e.preventDefault();
    // Clear token
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userName');
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('userName');
    sessionStorage.removeItem('refreshToken');

    // Redirect to login page
    window.location.href = '../pages/anim.html';
});

$("#adminEmail").text(localStorage.getItem('email'))

const accessToken = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");

    if (!accessToken) {

        window.location.href = "../../logging-expired.html";
        

    }

const stringOnlyPattern = /^[A-Za-z\s]+$/;



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




});