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
const stringOnlyPattern = /^[A-Za-z\s]+$/;
const addBtn = $("#addStationBtn");

let currentPage = 1;
let totalPages = 0;
const maxVisiblePages = 5;
let lastColorIndex = -1; 
let currentKeyword = "";


function getRandomColor() {
  let index;
  do {
    index = Math.floor(Math.random() * iconColors.length);
  } while (index === lastColorIndex); 
  lastColorIndex = index;
  return iconColors[index];
}


fetchStations(currentPage);




/////////////////////////////////////////// register model //////////////////////////////////////////////////////////////////////////////////////////////

const registerModal = $("#stationRegisterModal");
const closeRegisterBtn = $("#closeRegisterModal");
const cancelRegisterBtn = $("#cancelRegisterBtn");

closeRegisterBtn.on("click", closeRegisterModal);
cancelRegisterBtn.on("click", closeRegisterModal);

addBtn.on("click", function () {
  registerModal.addClass("active");
});

function closeRegisterModal() {
  registerModal.removeClass("active");
  resetRegisterFields();
}

registerModal.on("click", function (e) {
  if ($(e.target).is(registerModal)) {
    registerModal.removeClass("active");
  }
});

$("#stationRegisterForm input").on("keydown", function (e) {
    if (e.key === "Enter") {
        e.preventDefault();

    }
});

$("#stationRegisterForm").on("submit", async function (e) {
    e.preventDefault();

    if (!$("#stationDistrict").val().trim() || !$("#stationProvince").val().trim()) {
        const location = $("#stationLocation").val().trim();
        const splitLocationData = location.split(",").map(s => s.trim());

        if (splitLocationData.length >= 2) {
            $("#stationDistrict").val(splitLocationData[0]);
            $("#stationProvince").val(splitLocationData[1]);
        } else {
            console.warn("Unexpected location format: " + location);
        }
    }

    const stationName = $("#stationName").val().trim();
    const stationCode = $("#stationCode").val().trim();
    const stationDistrict = $("#stationDistrict").val().trim();
    const stationProvince = $("#stationProvince").val().trim();
    const platformSelection = $("#platformNumbersSelection").val();
    const platformLength = $("#platformLength").val().trim();

    let facilities = [];
    $("input[name='facilities']:checked").each(function() {
        facilities.push($(this).val());
    });
    facilities = facilities.join(", ");

    const rawData = JSON.stringify({
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
        headers: { "Content-Type": "application/json" },
        body: rawData,
        redirect: "follow"
    };

    try {
        const { response, result } = await fetchWithTokenRefresh(
            "http://localhost:8080/api/v1/raillankapro/station/register",
            requestOptions
        );

        if (!response || !result) return;

        console.log(result);

        if (result.code === 409) {
            $("#stationNameMsg").text(result.message).addClass("text-red-500");
            return;
        }

        if (result.code === 201) {
            toastr.success(result.data);
            fetchStations(currentPage); 
            closeRegisterModal();       
        }

    } catch (error) {
        console.error("🔥 Station registration error:", error);
        $("#stationTable tbody").html(
            '<tr><td colspan="6" class="px-6 py-4 text-center text-red-500">Error registering station</td></tr>'
        );
    }
});




/////////////////////////////////////////// update model //////////////////////////////////////////////////////////////////////////////////////////////

const updateModal = $("#stationUpdateModal");
const closeUpdateBtn = $("#closeUpdateModal");
const cancelUpdateBtn = $("#cancelUpdateBtn");

closeUpdateBtn.on("click", closeUpdateModal);
cancelUpdateBtn.on("click", closeUpdateModal);

function closeUpdateModal() {
  updateModal.removeClass("active");
  resetUpdateFields();
}

updateModal.on("click", function(e) {
          if ($(e.target).is(updateModal)) {
           closeUpdateModal();
          }
});

$(document).on("click", "#updateStationBtn", async function() {
    const id = $(this).data("id");
    $("#stationUpdateModal").addClass("active");

    const url = `http://localhost:8080/api/v1/raillankapro/station/getStationById?id=${id}`;

    try {
        const { response, result } = await fetchWithTokenRefresh(url, {
            method: "GET",
            redirect: "follow"
        });

        if (!response || !result) return;

        $("#updateStationId").val(result.data.stationId);
        $("#updateStationName").val(result.data.name);
        $("#updateStationCode").val(result.data.stationCode);
        $("#updatePlatformNumbersSelection").val(result.data.noOfPlatforms);
        $("#updatePlatformLength").val(result.data.platformLength);
        $("#updateStationDistrict").val(result.data.district);
        $("#updateStationProvince").val(result.data.province);

        const facilities = result.data.otherFacilities
            ? result.data.otherFacilities.split(",").map(f => f.trim())
            : [];

        facilities.forEach(f => {
            $(`input[name='updateFacilities'][value='${f}']`).prop("checked", true);
        });

        if (result.data.inService) {
            $("#inServiceRadio").prop("checked", true);
        } else {
            $("#outOfServiceRadio").prop("checked", true);
        }

    } catch (error) {
        console.error(error);
    }
});


$("#stationUpdateForm").on("submit", async function (e) {
    e.preventDefault();

    const stationId = $("#updateStationId").val().trim();
    const stationName = $("#updateStationName").val().trim();
    const stationCode = $("#updateStationCode").val().trim();
    const platformSelection = $("#updatePlatformNumbersSelection").val();
    const platformLength = $("#updatePlatformLength").val().trim();
    const stationDistrict = $("#updateStationDistrict").val().trim();
    const stationProvince = $("#updateStationProvince").val().trim();

    let facilities = [];
    $("input[name='updateFacilities']:checked").each(function() {
        facilities.push($(this).val());
    });
    facilities = facilities.join(", ");

    const statusValue = $("input[name='updateStatus']:checked").val();  
    const boolChecked = (statusValue === "true");

    const raw = JSON.stringify({
        "stationId": stationId,
        "name": stationName,
        "stationCode": stationCode,
        "district": stationDistrict,
        "province": stationProvince,
        "noOfPlatforms": platformSelection,
        "platformLength": platformLength,
        "inService": boolChecked,
        "otherFacilities": facilities
    });

    try {
        const { response, result } = await fetchWithTokenRefresh(
            "http://localhost:8080/api/v1/raillankapro/station/update",
            {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: raw,
                redirect: "follow"
            }
        );

        if (!response || !result) return;

        if (result.code === 409) {
            $("#updateStationNameMsg").text(result.message).addClass("text-red-500");
            return;
        }

        if (result.code === 200) {
            $("#stationUpdateModal").removeClass("active");
            toastr.success(result.data);
            fetchStations(currentPage);
            resetUpdateFields();
        }

    } catch (error) {
        console.error(error);
    }
});






/////////////////////////////////////// delete station ///////////////////////////////////////////////////////////////////////////////////////////////////

$(document).on("click", "#deleteStationBtn", async function () {
    const id = $(this).data("id");

    const resultConfirm = await Swal.fire({
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

    if (!resultConfirm.isConfirmed) return;

    try {
        const { response, result } = await fetchWithTokenRefresh(
            `http://localhost:8080/api/v1/raillankapro/station/delete?id=${id}`,
            { method: "PUT", redirect: "follow" }
        );

        if (!response || !result) return;

        fetchStations(currentPage);
        console.log(result);

        toastr.success(result.data);
    } catch (error) {
        console.error(error);
        toastr.error("Something went wrong while deleting.");
    }
});






/////////////////////////////////////////////////// filter station /////////////////////////////////////////////////////////////////////////////////
$("#filterStation").on("input", function () {
    const keyword = encodeURIComponent($(this).val().trim());
    currentKeyword = keyword; 
    currentPage = 1;
    fetchStations(currentPage, currentKeyword);
});






////////////////////////////////////// suggest location //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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





////////////////////////////////////// pagination buttons ////////////////////////////////////////////////////////////////////////////
function updatePaginationControls(currentPage, totalPages) {
      const $pageNumbers = $('#pageNumbers');
      $pageNumbers.empty();

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
        $pageNumbers.append(createPageButton(1));
        if (startPage > 2) {
          $pageNumbers.append('<span class="px-3 py-1">...</span>');
        }
      }

      for (let i = startPage; i <= endPage; i++) {
        $pageNumbers.append(createPageButton(i));
      }

      if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
          $pageNumbers.append('<span class="px-3 py-1">...</span>');
        }
        $pageNumbers.append(createPageButton(totalPages));
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
      fetchStations(currentPage, currentKeyword);
    }
  });
}

$("#btnFirst").on("click", () => {
  if (currentPage > 1) {
    currentPage = 1;
    fetchStations(currentPage, currentKeyword);
  }
});

$("#btnBack").on("click", () => {
  if (currentPage > 1) {
    currentPage -= 1;
    fetchStations(currentPage, currentKeyword);
  }
});

$("#btnNext").on("click", () => {
  if (currentPage < totalPages) {
    currentPage += 1;
    fetchStations(currentPage, currentKeyword);
  }
});

$("#btnLast").on("click", () => {
  if (currentPage < totalPages) {
    currentPage = totalPages;
    fetchStations(currentPage, currentKeyword);
  }
});




////////////////////////////////////// change status //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

$(document).on("click", ".fa-toggle-on, .fa-toggle-off", async function () {
    const $btn = $(this);
    const $row = $btn.closest("tr");
    const $statusSpan = $row.find("td:nth-child(5) span");

    let newStatus;
    if ($btn.hasClass("fa-toggle-on")) {
        $btn.removeClass("fa-toggle-on").addClass("fa-toggle-off");
        newStatus = false;
    } else {
        $btn.removeClass("fa-toggle-off").addClass("fa-toggle-on");
        newStatus = true;
    }

    const stationId = $row.find("td:nth-child(1) div.text-sm.text-gray-500").text().trim(); 

    try {
        const { response, result } = await fetchWithTokenRefresh(
            `http://localhost:8080/api/v1/raillankapro/station/changeInServiceStatus/${stationId}/${newStatus}`,
            { method: "PUT", redirect: "follow" }
        );

        if (!response || !result) return;

        fetchStations(currentPage,currentKeyword);

        if (result.data) {
            toastr.success(result.message);
        } else {
            toastr.warning(result.message);
        }

    } catch (error) {
        console.error(error);
        fetchStations(currentPage,currentKeyword);
    }
});




///////////////////////////////////////// fetch stations ///////////////////////////////////////////////////////////////////////////
async function fetchStations(page, keyword = "") {
    let url = "";
    if (keyword && keyword.length >= 2) {
        url = `http://localhost:8080/api/v1/raillankapro/station/filter/${page}/7?keyword=${keyword}`;
    } else {
        url = `http://localhost:8080/api/v1/raillankapro/station/getAll/${page}/7`;
    }
    
    try{
      const { response, result } = await fetchWithTokenRefresh(url, {
                method: "GET",
                redirect: "follow"
            });

      if (!response || !result) {
          return;
      }

      console.log(result);

      const stations = result.data;
      totalPages = result.totalPages;

      $('#currentPage').text(result.startNumber);
      $('#totalPage').text(result.totalItems);
      $("#selectedLastRowData").text(result.endNumber);

      $("#stationTable tbody").empty();
      if (stations.length === 0) {
          $("#stationTable tbody").append(
            `<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">
                No stations found
              </td></tr>`
          );
      } else {
          stations.forEach(station => {
              const randomColor = getRandomColor();
              let statusText = station.inService ? "in-service" : "out-of-service";
              let statusClass = station.inService ? "status-active" : "status-inactive";

              $("#stationTable tbody").append(`
                <tr class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="flex-shrink-0 h-10 w-10 bg-${randomColor}-100 rounded-full flex items-center justify-center">
                        <i class="fas fa-train text-${randomColor}-600"></i>
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">${station.name}</div>
                        <span class ="text-sm text-gray-500">ID:</span>
                        <div class="text-sm text-gray-500 inline">${station.stationId}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm font-mono">${station.stationCode}</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm text-gray-900">${station.district}</div>
                    <div class="text-sm text-gray-500">${station.province}</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ${station.noOfPlatforms} platform(s)
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}">
                      ${statusText}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div class="flex space-x-2">
                      <button id="updateStationBtn" class="text-blue-600 hover:text-blue-900" data-id="${station.stationId}">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button id="deleteStationBtn" class="text-red-600 hover:text-red-900" data-id="${station.stationId}">
                        <i class="fas fa-trash"></i>
                      </button>
                      <button class="text-gray-600 hover:text-gray-900">
                        <i class="fas ${station.inService ? "fa-toggle-on" : "fa-toggle-off"}"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              `);
          });
      }

      updatePaginationControls(page, totalPages);

    } catch (error) {
            console.log(err);
            $("#stationTable tbody").html(
              '<tr><td colspan="6" class="px-6 py-4 text-center text-red-500">Error loading data</td></tr>'
            );
    }
}





//////////////////////// validation //////////////////////////////////////////////////////////////////////////////
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
        e.preventDefault(); 
    }
});

$("#updateStationCode").on("input", function () {
    let val = $(this).val();

    val = val.replace(/[^A-Za-z]/g, "");

    val = val.toUpperCase();

    if (val.length > 3) {
      val = val.slice(0, 3);
    }

    $(this).val(val);
});

$("#updateStationCode").on("keypress", function (e) {
    if ($(this).val().length >= 3) {
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



//////////////////////// reset fields //////////////////////////////////////////////////////////////////////////////

function resetRegisterFields() {
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

function resetUpdateFields() {
    $("#updateStationId").val("")
    $("#updateStationName").val("")
    $("#updateStationCode").val("")
    $("#updatePlatformNumbersSelection").val("")
    $("#updatePlatformLength").val("")
    
    $("input[name='updateFacilities']").prop("checked", false);
    $("input[name='updateStatus']").prop("checked", false); 
    $("#updateStationNameMsg").text("").removeClass("text-red-500")



}





