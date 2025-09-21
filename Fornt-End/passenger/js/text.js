$(document).ready(function() {

    function clearAllTokens() {
        localStorage.removeItem('passengerAccessToken');
        localStorage.removeItem('passengerRefreshToken');
        localStorage.removeItem('passengerUserName');
        sessionStorage.removeItem('passengerAccessToken');
        sessionStorage.removeItem('passengerUserName');
        sessionStorage.removeItem('passengerRefreshToken');
    }

    let PASSENGER_ID = "";
    let PRICE="";
    let SHEDULE="";
    async function refreshAccessToken() {
        const refreshToken = localStorage.getItem('passengerRefreshToken') || sessionStorage.getItem('passengerRefreshToken');
        
        if (!refreshToken) {
            console.log('❌ No refresh token found');
            window.location.href = "/passenger/pages/logging-expired.html";
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
                
                if (localStorage.getItem('passengerRefreshToken')) {
                    localStorage.setItem('passengerRefreshToken', newAccessToken);
                    localStorage.setItem('passengerRefreshToken', newRefreshToken);
                } else if (sessionStorage.getItem('passengerRefreshToken')) {
                    sessionStorage.setItem('passengerRefreshToken', newAccessToken);
                    sessionStorage.setItem('passengerRefreshToken', newRefreshToken);
                }

                console.log('✅ Token refreshed successfully:', newAccessToken);
                return newAccessToken;
            } else {
                console.log('⚠️ Refresh token expired or invalid:', result.message);
                clearAllTokens();
                window.location.href = "/passenger/pages/logging-expired.html";
                return null;
            }
        } catch (error) {
            console.error('🔥 Error refreshing token:', error);
            clearAllTokens();
            window.location.href = "/passenger/pages/logging-expired.html";
            return null;
        }
    }

    async function fetchWithTokenRefresh(url, options = {}) {
        let accessToken = localStorage.getItem('passengerAccessToken') || sessionStorage.getItem('passengerAccessToken');
        
        if (!accessToken) {
            console.log("❌ No access token found. Redirecting...");
            window.location.href = "/passenger/pages/logging-expired.html";
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

    updateAuthUI();
    setupProfileModal();
    setupEditProfileModal();
    setupChangePasswordModal();
    // handlePaymentProcess();



  
  // Global variables
  let currentStep = 1;
  let selectedClass = null;
  let selectedSeats = [];
  let bookedSeatsMap = {}; 
  let adultCount = 1;
  let childCount = 0;
  let currentCarriage = 1;
  let maxSeats = 1;

  let selectedTrain = "";
  let departuteTime = ""



  let currentPage = 1;
  let totalPages = 0;
  const maxVisiblePages = 3;
  let currentKeyword = "";
  let currentClassFilter = null;
  let lastColorIndex = -1; 
  const $video = $("#heroVideo");
  const $heroSection = $("#heroSection");


  dateRestrictions();
  loadStationsToSelection($("#departureStation"), null, null, null);


  dateRestrictions();
  loadStationsToSelection($("#departureStation"), null, null, null);

  function isHeroInView() {
      const rect = $heroSection[0].getBoundingClientRect();
      return rect.top < window.innerHeight && rect.bottom > 0;
  }

  function checkHeroVideo() {
      if (isHeroInView()) {
          if ($video[0].paused) $video[0].play();
      } else {
          if (!$video[0].paused) $video[0].pause();
      }
  }

    checkHeroVideo();

    $(window).on("scroll resize", checkHeroVideo);

    $video.on("ended", function () {
        if (isHeroInView()) {
            scrollToBooking();
        }
    });

    $("#homeLink").on("click", function (e) {
        e.preventDefault();
        $video[0].currentTime = 0;
        $video[0].play();
    });


    $(document).on('click', '.view-details-btn', function(e) {
        e.stopPropagation(); 
        const $trainCard = $(this).closest('.train-card');
        const $detailsSection = $trainCard.find('.train-details');

        $detailsSection.toggleClass('active'); 
        $(this).toggleClass('active');
    });



  window.scrollToBooking = function() {
    $("html, body").animate(
      { scrollTop: $("#booking").offset().top },
      600
    );
  }

  $("#theme-toggle").click(function () {
    $("html").toggleClass("dark");
    const isDark = $("html").hasClass("dark");
    localStorage.setItem("theme", isDark ? "dark" : "light");

    const icon = $(this).find("i");
    if (isDark) {
      icon.removeClass("fa-sun").addClass("fa-moon");
    } else {
      icon.removeClass("fa-moon").addClass("fa-sun");
    }
  });

  if (localStorage.getItem("theme") === "dark") {
    $("html").addClass("dark");
    $("#theme-toggle i").removeClass("fa-sun").addClass("fa-moon");
  }

  $("#mobile-menu-btn").click(function () {
    $("#mobile-menu").toggleClass("hidden");
  });

  $(".nav-link").click(function (e) {
    e.preventDefault();
    const page = $(this).data("page");

    $(".page").removeClass("active");
    $(`#${page}-page`).addClass("active");

    $(".nav-link")
      .removeClass("text-primary-600")
      .addClass("text-gray-600 dark:text-gray-300");

    $(this)
      .addClass("text-primary-600")
      .removeClass("text-gray-600 dark:text-gray-300");

    $("#mobile-menu").addClass("hidden");
    $("html, body").animate({ scrollTop: 0 }, "smooth");
  });

    $("#adult-minus").click(function () {
      if (adultCount > 1) {
        adultCount--;
        $("#adult-count").text(adultCount);
        updateSummary();
      }
    });

    $("#adult-plus").click(function () {
      if ((childCount + adultCount) < 5) {
        adultCount++;
      }
      $("#adult-count").text(adultCount);
      updateSummary();
    });

    $("#child-minus").click(function () {
      if (childCount > 0) {
        childCount--;
        $("#child-count").text(childCount);
        updateSummary();
      }
    });

    $("#child-plus").click(function () {
       if ((childCount + adultCount) < 5) {
        childCount++;
      }
      $("#child-count").text(childCount);
      updateSummary();
    });


    $("#departureStation").change(function() {
      const selectedDeparture = $(this).val();
      const selectedArrival = $("#arrivalStation").val(); // preserve current arrival

      loadStationsToSelection($("#arrivalStation"), selectedDeparture, null, selectedArrival);
    });

    $("#arrivalStation").change(function() {
      const selectedArrival = $(this).val();
      const selectedDeparture = $("#departureStation").val(); // preserve current departure

      loadStationsToSelection($("#departureStation"), null, selectedArrival, selectedDeparture);
    });

    function loadStationsToSelection(selection, departureStation = null, arrivalStation = null, selectedStation = null) {
      const requestOptions = {
        method: "GET",
        redirect: "follow"
      };

      fetch("http://localhost:8080/api/v1/raillankapro/auth/stations", requestOptions)
        .then((response) => response.json())
        .then((result) => {
          if (result.code === 200) {
            const stations = result.data;
            stations.sort((a, b) => a.name.localeCompare(b.name));

            selection.empty();

            selection.append('<option value="" disabled selected>Select Station</option>');

            stations.forEach((station) => {
              let isDisabled = false;
              let disableText = "";

              if (!station.inService) {
                isDisabled = true;
                disableText = " - out of service";
              }

              if (departureStation && station.name === departureStation) {
                isDisabled = true;
                disableText = " - (Origin)";
              }

              if (arrivalStation && station.name === arrivalStation) {
                isDisabled = true;
                disableText = " - (Destination)";
              }

              const isSelected = selectedStation && selectedStation === station.name ? "selected" : "";

              const optionHtml = `<option value="${station.name}" ${isDisabled ? "disabled" : ""} ${isSelected}>${station.name}${disableText}</option>`;
              selection.append(optionHtml);
            });
          }
        })
        .catch((error) => console.error(error));
    }

    function dateRestrictions() {
      const $dateInput = $("#myDate");

      const today = new Date();
      const dd = String(today.getDate()).padStart(2, "0");
      const mm = String(today.getMonth() + 1).padStart(2, "0");
      const yyyy = today.getFullYear();
      const todayStr = `${yyyy}-${mm}-${dd}`;

      const maxDateObj = new Date();
      maxDateObj.setDate(today.getDate() + 2);
      const maxDd = String(maxDateObj.getDate()).padStart(2, "0");
      const maxMm = String(maxDateObj.getMonth() + 1).padStart(2, "0");
      const maxYyyy = maxDateObj.getFullYear();
      const maxDateStr = `${maxYyyy}-${maxMm}-${maxDd}`;

      $dateInput.attr("min", todayStr);
      $dateInput.attr("max", maxDateStr);
    }
    


    // $("#search-trains").click(nextStep);
    $(".select-train").click(nextStep);
    $(".prev-step").click(prevStep);
    // $("#to-payment").click(nextStep);

    $("#to-payment").click(function() {
      if (!selectedClass) {
      return;
    }
      console.log(selectedSeats);
      
      updateSummary();
      nextStep();

      
    });

    // Class selection
    $(".select-class").click(function () {
      const className = $(this).data("class");

      $(".train-class").removeClass("selected");
      $(this).closest(".train-class").addClass("selected");
      selectedClass = className;

      const classNames = {
        first: "First Class",
        second: "Second Class",
        third: "Third Class",
      };
      $("#selected-class").text(classNames[className]);
      const priceSelectors = {
        first: "#firstClassPrice",
        second: "#secondClassPrice",
        third: "#thirdClassPrice"
      };

      const priceText = $(priceSelectors[className]).text();
      $("#total-price").text(priceText)
      PRICE = priceText; 


      console.log(`Selected class: ${className}`);

      if (className === "first") {
        $("#seat-modal").addClass("active");
        $("body").css("overflow", "hidden");
        selectedSeats = []; 
        generateSeatMap();
      } else {
        selectedSeats = [];
        $("#selected-seats").text("Not applicable");
      }
    });

    $("#close-modal").click(function () {
      $("#seat-modal").removeClass("active");
      $("body").css("overflow", "auto"); 
      
    });

    $("#confirm-seats").click(function () {     
      if (Object.keys(selectedSeats).length === 0) {
          return; 
      }
      console.log(`selectedSeats: ${selectedSeats}`);
      
      const btn = $(this);
      const originalText = btn.html();
        btn.html(`
        <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Confirming...
      `);

      setTimeout(() => {
        btn.html(`
        <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Redirecting to payment...
      `);
      btn.prop("disabled", true);
      }, 900);

      setTimeout(() => {
      $("#seat-modal").removeClass("active");
      $("body").css("overflow", "auto"); 
        updateSelectedSeats();
        nextStep();
        btn.html(originalText);
        btn.prop("disabled", false);
      }, 1800);

      updateSummary();

    });



    $("#paymentForm").on("submit", async function (e) {
      e.preventDefault();
      let departureStation;
      let destinationStation
      let travelClass;
      let travelDate;
      let schedule;
      let finalAdultCount;
      let finalChildCount;
      let amountAsDouble;
      let seatObjects =[];
      let firstName = "";
      let lastName = "";
      let email = "";
      let phone = "";
      let nic = "";

      const userName = localStorage.getItem("passengerUserName") || sessionStorage.getItem("passengerUserName");

      const requestOptions = {
        method: "GET",
        redirect: "follow"
      };

      const {response,result} = await fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/passenger/get/id/by/username?username=${userName}`, requestOptions)

      if (!response || !result) return;

      if (result.code === 200) {
          PASSENGER_ID = result.data;
          departureStation = $("#departureStation").val();
          destinationStation = $("#arrivalStation").val();
          travelClass = selectedClass;
          travelDate = $("#myDate").val();
          schedule = SHEDULE;
          finalAdultCount = adultCount;
          finalChildCount = childCount;
          let numericPart = PRICE.replace(/[^\d.]/g, ""); 
          console.log("numaric part : "+numericPart);
          
          amountAsDouble = parseFloat(numericPart);
          const carriageMap = {
            1: "ONE",
            2: "TWO",
            3: "THREE",
            4: "FOUR"
          };
          seatObjects = [];
          Object.keys(selectedSeats).forEach(carriageNum => {
          let carriageName = carriageMap[carriageNum] || carriageNum;
            selectedSeats[carriageNum].forEach(seat => {
              seatObjects.push({
                carriage: carriageName,
                seat: seat
              });
            });
          });

          firstName = $("#payFirstName").val();
          lastName = $("#payLastName").val();
          email = $("#payEmail").val();
          phone = $("#payPhone").val();
          nic = $("#payNic").val();


          console.log("🟢 Departure Station:", departureStation);
          console.log("🟢 Destination Station:", destinationStation);
          console.log("🟢 Travel Class:", travelClass.toUpperCase());
          console.log("🟢 Travel Date:", travelDate);
          console.log("🟢 Schedule ID:", schedule);
          console.log("🟢 Adult Count:", finalAdultCount);
          console.log("🟢 Child Count:", finalChildCount);
          console.log("🟢 Total Amount:", amountAsDouble);
          console.log("🟢 Passenger ID:", PASSENGER_ID);
          console.log("🟢 Selected Seats:", seatObjects);
          console.log("🟢 User Name:", userName);
          console.log("🟢 First Name:", firstName);
          console.log("🟢 Last Name:", lastName);
          console.log("🟢 Email:", email);
          console.log("🟢 Phone:", phone);
          console.log("🟢 NIC/Passport:", nic);

          

    
      }

      const raw = JSON.stringify({
         "orderAmount": parseFloat(PRICE.replace(/[^\d.]/g, ""))
      });

      const requestOptionsforHash = {
        method: "POST",
         headers: {
                "Content-Type": "application/json"  
        },
        body: raw,
        redirect: "follow"
      };


      const {response: hashResponse, result: hashResult} = await fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/payments/generate-hash?username=${userName}`, requestOptionsforHash)

      if (!hashResponse || !hashResult) return;

  
      if (hashResult.code === 200) {
          console.log("🟢 Hash Generation Response:");
          
          const generatedHash = hashResult.data;
          const personalDetails = generatedHash.personalDetail
          console.log(personalDetails);
          
          console.log("Generated Hash:", generatedHash);
          
          const raw = JSON.stringify({
            "passengerId": PASSENGER_ID,
            "scheduleId": schedule,
            "travelDate": travelDate,
            "travelClass": travelClass.toUpperCase(),
            "adultCount": finalAdultCount,
            "childCount": finalChildCount,
            "totalAmount": amountAsDouble,
            "departureStation": departureStation,
            "destinationStation": destinationStation,
            "selectedSeat": seatObjects,
            "payeeInfo": {
                "firstName": firstName,
                "lastName": lastName,
                "nicOrPassport": nic,
                "orderId": orderId,
                "email": email,
                "phoneNumber": phone
            }
          });

          var payment = {
            "sandbox": true,
            "merchant_id": generatedHash.merchantId,    
            "return_url": "http://localhost:5500/passenger/pages/booking-success.html",  
            "cancel_url": "http://localhost:5500/", 
            "notify_url": "http://localhost:5500/",
            "order_id": generatedHash.orderId,
            "items": personalDetails.itemName,
            "amount": generatedHash.orderAmount.toFixed(2),
            "currency": generatedHash.currency,
            "hash": generatedHash.hash, 
            "first_name": personalDetails.firstName,
            "last_name": personalDetails.lastName,
            "email": personalDetails.email,
            "phone": personalDetails.phone,
            "address": personalDetails.city,
            "city": personalDetails.city,
            "country": "Sri Lanka",
            "delivery_address": personalDetails.city,
            "delivery_city": personalDetails.city,
            "delivery_country": "Sri Lanka",
            "data": row,
            "custom_2": ""
          };

          



          payhere.onCompleted =  function onCompleted(orderId) {
    // console.log("Payment completed. OrderID:" + orderId);
    
    // try {
    //     // Variables හරියටම set වෙලා ඇත්ද check කරන්න
    //     if (!PASSENGER_ID || !schedule || !travelDate) {
    //         console.error("❌ Required variables not set:", {
    //             PASSENGER_ID, schedule, travelDate
    //         });
    //         return;
    //     }

    //     const raw = JSON.stringify({
    //         "passengerId": PASSENGER_ID,
    //         "scheduleId": schedule,
    //         "travelDate": travelDate,
    //         "travelClass": travelClass.toUpperCase(),
    //         "adultCount": finalAdultCount,
    //         "childCount": finalChildCount,
    //         "totalAmount": amountAsDouble,
    //         "departureStation": departureStation,
    //         "destinationStation": destinationStation,
    //         "selectedSeat": seatObjects,
    //         "payeeInfo": {
    //             "firstName": firstName,
    //             "lastName": lastName,
    //             "nicOrPassport": nic,
    //             "orderId": orderId,
    //             "email": email,
    //             "phoneNumber": phone
    //         }
    //     });

    //     console.log("📤 Sending booking request:", raw);

    //     const requestOptions = {
    //         method: "POST",
    //         headers: {
    //             "Content-Type": "application/json"
    //         },
    //         body: raw,
    //         redirect: "follow"
    //     };

    //     console.log("✅ FETCH EKA LAGAT AWA");
        
    //     const {response, result} = await fetchWithTokenRefresh(
    //         "http://localhost:8080/api/v1/raillankapro/booking/place", 
    //         requestOptions
    //     );

    //     console.log("✅ FETCH EKE RESULT EKA LAGAT AWA");
    //     console.log("Response:", response);
    //     console.log("Result:", result);
        
    //     if (result && result.code === 201) {
    //         alert("✅ Booking successful! Your tickets have been booked.");
    //     } else {
    //         console.error("❌ Booking failed:", result);
    //         alert("❌ Booking failed. Please try again.");
    //     }
        
    // } catch (error) {
    //     console.error("❌ Error in onCompleted:", error);
    //     alert("❌ An error occurred. Please try again.");
    // }
    };

      // Payment window closed
      payhere.onDismissed = function onDismissed() {
          // Note: Prompt user to pay again or show an error page
          console.log("Payment dismissed");
      };

      // Error occurred
      payhere.onError = function onError(error) {
          // Note: show an error page
          console.log("Error:"  + error);
      };
      payhere.startPayment(payment);


      }
      

     
   
       

    });

    



  // Booking functions
  function nextStep() {
    if (currentStep < 4) {
      $(`#step-${currentStep}`).removeClass("active");
      currentStep++;
      $(`#step-${currentStep}`).addClass("active");
      updateProgressBar();
    }
  }

  function prevStep() {
    if (currentStep > 1) {
      $(`#step-${currentStep}`).removeClass("active");
      currentStep--;
      $(`#step-${currentStep}`).addClass("active");
      updateProgressBar();
    }
  }

  function updateProgressBar() {
    const progress = ((currentStep - 1) / 3) * 100;
    $("#progress-fill").css("width", `${progress}%`);
  }

  function generateSeatMap() {
    // Generate seats for both carriages
      generateCarriageSeats(1);
      generateCarriageSeats(2);

      // Set up carriage tab switching
      document.querySelectorAll('.carriage-tab').forEach(tab => {
        tab.addEventListener('click', function() {
          const carriageId = this.getAttribute('data-carriage');
          switchCarriage(carriageId);
      });
    });

    $(".seat").off("click").on("click", function () {
      if ($(this).hasClass("occupied")) return; // occupied seat select karanna epa

      let selectedSeats = $(".seat.selected").length;

      if ($(this).hasClass("selected")) {
        $(this).removeClass("selected");
      } else {
        if (selectedSeats < maxSeats) {
          $(this).addClass("selected");
        } 
      }

      updateSelectedSeats();
    });
  }

  function generateCarriageSeats(carriageId) {
      const $seatMap = $(`.seat-map[data-carriage="${carriageId}"]`);
      $seatMap.empty();

      const rows = 5;
      const cols = 4;

      for (let i = 0; i < rows; i++) {
          for (let j = 0; j < cols; j++) {
              const seatLabel = `${String.fromCharCode(65 + i)}${j + 1}`;
              const $seat = $("<div>")
                  .addClass("seat")
                  .text(seatLabel)
                  .attr("data-seat", seatLabel)
                  .attr("data-carriage", carriageId);

              // booked seat check
              if (bookedSeatsMap[carriageId] && bookedSeatsMap[carriageId].includes(seatLabel)) {
                  $seat.addClass("occupied"); 
              }

              $seatMap.append($seat);
          }
      }
  }

  function switchCarriage(carriageId) {
    // Update active tab
    $(".carriage-tab").each(function () {
      const $tab = $(this);
      if ($tab.data("carriage") == carriageId) {
        $tab
          .addClass("border-b-2 border-primary-600 text-primary-600")
          .removeClass("text-gray-500 dark:text-gray-400");
      } else {
        $tab
          .removeClass("border-b-2 border-primary-600 text-primary-600")
          .addClass("text-gray-500 dark:text-gray-400");
      }
    });

    // Show selected carriage, hide others
    $(".carriage-seat-map").each(function () {
      const $map = $(this);
      if ($map.data("carriage") == carriageId) {
        $map.removeClass("hidden").addClass("active");
      } else {
        $map.addClass("hidden").removeClass("active");
      }
    });

    currentCarriage = parseInt(carriageId);
  }

  function updateSelectedSeats() {
      
    // Reset selected seats object
    selectedSeats = {};

    // Get all selected seats from both carriages
    $(".seat.selected").each(function () {
      const $seat = $(this);
      const carriageId = $seat.data("carriage");
      const seatNumber = $seat.data("seat");

      if (!selectedSeats[carriageId]) {
        selectedSeats[carriageId] = [];
      }

      selectedSeats[carriageId].push(seatNumber);

          console.log(selectedSeats);

    });

    const $summaryElement = $("#selected-seats-summary");
    let summaryText = "";

    if (Object.keys(selectedSeats).length === 0) {
      summaryText = "No seats selected";
    } else {
      $.each(selectedSeats, function (carriageId, seats) {
        if (seats.length > 0) {
          summaryText += `Carriage ${carriageId}: ${seats.join(", ")}. `;
        }
      });
    }

    $summaryElement.text(summaryText);
  }

  function updateSummary() {
    const departure = $("#departureStation").val();
    const destination = $("#arrivalStation").val();
    const dateStr = $("#myDate").val(); // "2023-08-25"

    const date = new Date(dateStr);
    const options = { day: "2-digit", month: "short", year: "numeric" };
    const formattedDate = date.toLocaleDateString("en-GB", options); 

    $("#route").text(`${departure} to ${destination}`);
    $("#trainName").text(selectedTrain || "N/A");
    $("#summary-adults").text(adultCount);
    $("#summary-children").text(childCount);

    let seatSummaryText = "";

    if (Object.keys(selectedSeats).length === 0) {
      seatSummaryText = "No seats selected";
      if(selectedClass!="first") {
        seatSummaryText = "Seat Selection Not Avalable!"
      }
      console.log(selectedClass);
      
    } else {
      $.each(selectedSeats, function (carriageId, seats) {
        seatSummaryText += `CRR-${carriageId}: ${seats.join(", ")} | `;
      });
    }

    $("#selected-seats").text(seatSummaryText);
    $("#sumDate").text(formattedDate);
    $("#sumTime").text(departuteTime);
}




  $("#searchTrainTxtField").on("input", function () {
    $("#classFilter").val("");
      const keyword = encodeURIComponent($(this).val().trim());
      currentKeyword = keyword; 
      currentPage = 1;
      loadTrains(currentPage, currentKeyword,null);
  });
  

  $("#classFilter").on("change" , function(){
      $("#searchTrainTxtField").val("");
      const selectedClass = $(this).val();
       currentPage = 1;
      if(!selectedClass) {
        loadTrains(currentPage, null,null);
        return;
      }
      currentClassFilter = selectedClass;
     
      loadTrains(currentPage, null, currentClassFilter);

  });



  function loadTrains(page, keyword = null, classFilter = null) {
    let url = "";
    if (classFilter) {
        url = `http://localhost:8080/api/v1/raillankapro/auth/schedules/${page}/1?class=${classFilter}`;
    }else if (keyword && keyword.length >= 2) {
        url = `http://localhost:8080/api/v1/raillankapro/auth/schedules/${page}/1?train=${keyword}`;
    } else {
        url = `http://localhost:8080/api/v1/raillankapro/auth/schedules/${page}/1`;
    }

    const departureStation = $("#departureStation").val();
    const arrivalStation = $("#arrivalStation").val();
    const date = $("#myDate").val();

    console.log(departureStation);
    console.log(arrivalStation);
    
    
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    
    const raw = JSON.stringify({
      "departureStation": departureStation,
      "destinationStation": arrivalStation,
      "date": date,
      "adultCount": adultCount,
      "childCount": childCount
    });

    const requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow"
    };

    fetch(url, requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(`result is : ${result}`);
        console.log(result);
        console.log(url);
        
        
        if (result.code === 200) {

          const classColors = {
            "1st": { bg: "bg-red-50", text: "text-red-700", border: "border-red-100" },
            "2nd": { bg: "bg-blue-50", text: "text-blue-700", border: "border-blue-100" },
            "3rd": { bg: "bg-green-50", text: "text-green-700", border: "border-green-100" }
          };

          const trains = result.data;
          totalPages = result.totalPages;

          $("#pageStartNumber").text(result.startNumber);
          $("#pageEndCount").text(result.endNumber);
          $("#totalCount").text(result.totalItems);

          $("#compactTrainList").empty();

          if (trains.length === 0) {
              $("#compactTrainList").html(`
                  <div class="flex flex-col items-center justify-center py-12 px-4 text-center">
                    <div class="w-24 h-24 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-6">
                      <i class="fas fa-train text-4xl text-gray-400"></i>
                    </div>
                    <h3 class="text-xl font-semibold text-gray-700 dark:text-gray-300 mb-2">No Trains Available</h3>
                    <p class="text-gray-500 dark:text-gray-400 mb-6 max-w-md">
                      We couldn't find any trains matching your search criteria. Please try adjusting your departure time, date, or route.
                    </p>
                    <div class="flex flex-col sm:flex-row gap-3">
                      <button class="changeDetailsBtn px-6 py-3 bg-primary-600 hover:bg-primary-700 text-white rounded-lg transition-colors">
                        <i class="fas fa-calendar-alt mr-2"></i>Change Date
                      </button>
                      <button class="changeDetailsBtn px-6 py-3 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800 rounded-lg transition-colors">
                        <i class="fas fa-route mr-2"></i>Modify Route
                      </button>
                    </div>
                  </div>
                `);
              return;
          }
          trains.forEach((train) => {
            let statusText = train.status ? "Available" : "Nonavailable";
            let statusClass = train.status ? "green" : "orange";
            let status = train.status;
            let icon = status ? "fas fa-arrow-right" : "fa fa-ban";

            let classesHtml = "";
            if (train.trainClass) {
                classesHtml = train.trainClass.split(",").map(c => {
                    c = c.trim();
                    const colors = classColors[c] || { bg: "bg-gray-50", text: "text-gray-700", border: "border-gray-100" }; // default
                    return `
                        <span class="px-2.5 py-1 ${colors.bg} ${colors.text} text-xs font-medium rounded-full border ${colors.border}">
                            ${c}
                        </span>
                    `;
                }).join("");
            }

            let stopsHtml = "";
            train.intermediateStops.forEach((stop) => {
              stopsHtml += `
                <div>
                  <div class="flex justify-between items-center">
                    <div class="flex items-center">
                      <div class="w-2.5 h-2.5 rounded-full bg-primary-400 mr-3"></div>
                      <span class="text-sm">${stop.stationName}</span>
                    </div>
                    <span class="text-xs text-gray-500">
                      ${stop.arrivalTime} - ${stop.departureTime}
                    </span>
                  </div>
                  <p class="text-xs text-gray-500 mt-1 ml-5">
                    <i class="fas fa-tag text-xs mr-1"></i>${stop.stationFacilities}
                  </p>
                </div>
              `;
            });


            $("#compactTrainList").append(`
                <div
                  class="train-card bg-white dark:bg-gray-800 rounded-2xl p-5 cursor-pointer border border-gray-100 dark:border-gray-700 hover:shadow-lg transition-all duration-300 relative overflow-hidden"
                >
                  
                  <div
                    class="absolute top-0 left-0 w-1.5 h-full bg-gradient-to-b from-blue-500 to-blue-600"
                  ></div>

                  <!-- Main content -->
                  <div class="ml-3">
                    <div class="flex justify-between items-start mb-4">
                      <div>
                        <div class="flex items-center gap-2 mb-1">
                          <h4 class="train-name text-xl font-bold" data-name="${train.trainName}">${train.trainName}</h4>
                          <span
                            class="px-2 py-1 bg-${statusClass}-100 text-${statusClass}-700 text-xs font-medium rounded-full flex items-center"
                          >
                            <span
                              class="w-2 h-2 bg-${statusClass}-500 rounded-full mr-1"
                            ></span>
                            ${statusText}
                          </span>
                        </div>
                        <p class="text-sm text-gray-500 dark:text-gray-400">
                          <i class="far fa-calendar-alt mr-1"></i> ${date}
                        </p>
                      </div>

                      <div class="flex flex-col items-end">
                        <button
                        ${status ? "" : "disabled"}
                          class="btn-book bg-gradient-to-r from-primary-600 to-primary-700 text-white px-4 py-2 rounded-lg hover:shadow-lg transition-all duration-300 font-medium mb-2"
                        >
                          Book Now <i class="arrow-icon fas fa-arrow-right ml-1"></i>
                        </button>
                        <button
                          class="view-details-btn text-xs text-gray-500 hover:text-primary-600 flex items-center"
                        >
                          More details
                          <i class="fas fa-chevron-down ml-1 text-xs"></i>
                        </button>
                      </div>
                    </div>

                    <div class="flex items-center justify-between mb-5">
                      <div class="text-center">
                        <p class="station_departure_time text-2xl font-bold" data-departure="${train.selectedDepartureStationDepartureTime}">${train.selectedDepartureStationDepartureTime}</p>
                        <p
                          class="text-xs text-gray-500 dark:text-gray-400 mt-1"
                        >
                          ${train.selectedDepartureStation}
                        </p>
                      </div>

                      <div class="relative px-4 flex-1 max-w-xs">
                        <div class="flex items-center justify-center">
                          <div
                            class="h-px bg-gray-200 dark:bg-gray-700 w-full"
                          ></div>
                          <div class="mx-2 flex items-center">
                            <div
                              class="w-2 h-2 rounded-full bg-blue-500"
                            ></div>
                            <div class="w-12 h-px bg-blue-400"></div>
                            <div
                              class="w-2 h-2 rounded-full bg-blue-500"
                            ></div>
                          </div>
                          <div
                            class="h-px bg-gray-200 dark:bg-gray-700 w-full"
                          ></div>
                        </div>
                        <p
                          class="text-center text-xs text-gray-500 dark:text-gray-400 mt-1"
                        >
                        ${train.selectedScheduleDuration}
                        </p>
                      </div>

                      <div class="text-center">
                        <p class="text-2xl font-bold">${train.selectedArrivalStationArrivalTime}</p>
                        <p
                          class="text-xs text-gray-500 dark:text-gray-400 mt-1"
                        >
                          ${train.selectedDestinationStation}
                        </p>
                      </div>
                    </div>

                    <div class="flex justify-between items-center">
                      <div class="flex items-center gap-2">
                        <span class="text-sm text-gray-500 dark:text-gray-400"
                          >Classes:</span
                        >
                        <div class="flex gap-1">
                          ${classesHtml}
                        </div>
                      </div>

                      <div class="text-right">
                        <p class="text-xs text-gray-500 dark:text-gray-400">
                          From
                        </p>
                        <p class="text-xl font-bold text-primary-600">
                          LKR ${train.allCalculatedTicketPrice.thirdClass}
                        </p>
                      </div>
                    </div>
                  </div>

                  <!-- Expandable details section - Fixed with proper structure -->
                  <div
                    class="train-details mt-5 pt-5 border-t border-gray-100 dark:border-gray-700"
                  >
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <!-- Full route information -->
                      <div>
                        <h5
                          class="font-semibold mb-3 text-gray-800 dark:text-gray-200 flex items-center"
                        >
                          <i class="fas fa-route mr-2 text-primary-500"></i>
                          Full Route Schedule
                        </h5>
                        <div class="bg-gray-50 dark:bg-gray-900 rounded-xl p-4">
                          <!-- Timeline -->
                          <div class="relative">
                            <!-- Start -->
                            <div class="flex justify-between items-center mb-4">
                              <div class="flex items-center">
                                <div
                                  class="w-3 h-3 rounded-full bg-primary-500 mr-3"
                                ></div>
                                <span class="font-medium">${train.departureStationName}</span>
                              </div>
                              <span class="text-sm text-gray-500">${train.mainDepartureTime}</span>
                            </div>

                            <!-- Intermediate stops -->
                            <div
                              class="border-l-2 border-primary-200 dark:border-primary-800 ml-1.5 pl-5 space-y-4"
                            >
                            ${stopsHtml}
                            </div>

                            <!-- End -->
                            <div class="flex justify-between items-center mt-4">
                              <div class="flex items-center">
                                <div
                                  class="w-3 h-3 rounded-full bg-primary-500 mr-3"
                                ></div>
                                <span class="font-medium">${train.arrivalStationName}</span>
                              </div>
                              <span class="text-sm text-gray-500">${train.mainArrivalTime}</span>
                            </div>
                          </div>
                        </div>
                      </div>

                      <!-- Additional information -->
                      <div>
                        <h5
                          class="font-semibold mb-3 text-gray-800 dark:text-gray-200 flex items-center"
                        >
                          <i
                            class="fas fa-info-circle mr-2 text-primary-500"
                          ></i>
                          Train Information
                        </h5>
                        <div class="grid grid-cols-2 gap-4 mb-4">
                          <div>
                            <p
                              class="text-xs text-gray-500 dark:text-gray-400 mb-1"
                            >
                              Schedule ID
                            </p>
                            <p class="schedule-id text-sm font-medium" data-id="${train.scheduleId}">${train.scheduleId}</p>
                          </div>
                          <div>
                            <p
                              class="text-xs text-gray-500 dark:text-gray-400 mb-1"
                            >
                              Train Type
                            </p>
                            <p class="text-sm font-medium">${train.trainType}</p>
                          </div>
                          <div>
                            <p
                              class="text-xs text-gray-500 dark:text-gray-400 mb-1"
                            >
                              Frequency
                            </p>
                            <p class="text-sm font-medium">${train.scheduleFrequency}</p>
                          </div>
                          <div>
                            <p
                              class="text-xs text-gray-500 dark:text-gray-400 mb-1"
                            >
                              Total Duration
                            </p>
                            <p class="text-sm font-medium">${train.fullScheduleDuration}</p>
                          </div>
                        </div>

                        <h5
                          class="font-semibold mb-2 text-gray-800 dark:text-gray-200 flex items-center"
                        >
                          <i
                            class="fas fa-concierge-bell mr-2 text-primary-500"
                          ></i>
                          Station Facilities
                        </h5>
                        <div class="grid grid-cols-2 gap-2 text-xs">
                          <div class="flex items-center py-1">
                            <i
                              class="fas fa-restroom text-primary-500 mr-2 w-4"
                            ></i>
                            <span>Restrooms</span>
                          </div>
                          <div class="flex items-center py-1">
                            <i
                              class="fas fa-parking text-primary-500 mr-2 w-4"
                            ></i>
                            <span>Parking</span>
                          </div>
                          <div class="flex items-center py-1">
                            <i
                              class="fas fa-wifi text-primary-500 mr-2 w-4"
                            ></i>
                            <span>WiFi</span>
                          </div>
                          <div class="flex items-center py-1">
                            <i
                              class="fas fa-utensils text-primary-500 mr-2 w-4"
                            ></i>
                            <span>Food Court</span>
                          </div>
                          <div class="flex items-center py-1">
                            <i
                              class="fas fa-universal-access text-primary-500 mr-2 w-4"
                            ></i>
                            <span>Accessibility</span>
                          </div>
                          <div class="flex items-center py-1">
                            <i
                              class="fas fa-toilet text-primary-500 mr-2 w-4"
                            ></i>
                            <span>Lavatory</span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Additional booking button at bottom of expanded view -->
                    <div class="mt-6 text-center">
                      <button
                      ${status ? "" : "disabled"}
                        class="btn-book bg-gradient-to-r from-primary-600 to-primary-700 text-white px-8 py-3 rounded-xl hover:shadow-lg transition-all duration-300 font-medium"
                      >
                        Book This Train <i class="fas fa-arrow-right ml-2"></i>
                      </button>
                    </div>
                  </div>
                </div>

              `);

          });

        }

        updatePaginationControls(currentPage, totalPages);

      })
      .catch((error) => console.error(error));
  }




  $("#search-trains").on("click", function(e) {
      const departure = $("#departureStation").val();
      const arrival = $("#arrivalStation").val();
      const date = $("#myDate").val();
      const adults = parseInt($("#adult-count").text());
      const children = parseInt($("#child-count").text());
      maxSeats = adults + children;
      const isPassengerSelected = adults + children > 0;
      
      if (!departure) {
        $("#departureStation").focus(); 
        return;
      }

      if (!arrival) {
        $("#arrivalStation").focus();
        return;
      }

      if (!date) {
        $("#myDate").focus();
        return;
      }

  

      const btn = $(this);
      const originalText = btn.html();
      btn.html(`
        <svg class="animate-spin -ml-1 mr-2 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Searching...
      `);
      btn.prop("disabled", true);

      setTimeout(() => {
        console.table(departure,arrival,date,adults,children);
        nextStep();
        btn.html(originalText);
        btn.prop("disabled", false);
      }, 1500);

     
      currentPage = 1;
      loadTrains(currentPage,null,null);

      console.log({ 
        "departure":departure, 
        "arrival": arrival, 
        "adults count": adults, 
        "child count": children
      });
      
      

  });

  $(document).on("click", ".btn-book", function(e) {
    const trainCard = $(this).closest('.train-card');
    const scheduleId = trainCard.find(".schedule-id").data("id");
    SHEDULE = scheduleId

    const trainName = trainCard.find(".train-name").data("name");
    const darparture = trainCard.find(".station_departure_time").data("departure")
    console.log(trainName);
    selectedTrain = trainName;
    departuteTime = darparture;
    
    const departureStation = $("#departureStation").val();
    const destination = $("#arrivalStation").val();
    const date = $("#myDate").val();

    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
      "departure": departureStation,
      "destination": destination,
      "adultCount": adultCount,
      "childCount": childCount
    });

    const requestOptions1 = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow"
    };

    fetch(`http://localhost:8080/api/v1/raillankapro/auth/calc/clases/ticket/price?scheduleid=${scheduleId}`, requestOptions1)
    .then((response) => response.json())
    .then((result) => {
      if (result.code === 200) {
          $("#firstClassPrice").text(`LKR ${result.data.firstClass.toLocaleString()}`);
          $("#secondClassPrice").text(`LKR ${result.data.secondClass.toLocaleString()}`);
          $("#thirdClassPrice").text(`LKR ${result.data.thirdClass.toLocaleString()}`);
          updateSummary();
      }
    })
    .catch((error) => console.error(error));




    const requestOptions = {
      method: "GET",
      redirect: "follow"
    };

    bookedSeatsMap = {};

    fetch(`http://localhost:8080/api/v1/raillankapro/auth/get/booked/seats?traveldate=${date}&schedule=${scheduleId}`, requestOptions)
    .then((response) => response.json())
    .then((result) => {
        console.log(result)
        if (result.code === 200) {
              result.data.forEach(({ carriage, seat }) => {
              bookedSeatsMap[carriage] = bookedSeatsMap[carriage] || [];
              bookedSeatsMap[carriage].push(seat);
          });
        }
    })
    .catch((error) => console.error(error));

    console.log("Selected Schedule ID:", scheduleId);



    const availableClasses = trainCard.find(".flex.gap-1 span")
    .map(function () {
      let text = $(this).text().trim().toLowerCase(); 
      if (text.includes("1")) return "first";
      if (text.includes("2")) return "second";
      if (text.includes("3")) return "third";
      return text;
    })
    .get();

    console.log("Classes available: ", availableClasses);
    const classCount = availableClasses.length;

    const grid = $("#step-3 #canvas");


    grid.removeClass("md:grid-cols-3");


    if (classCount === 1) grid.addClass("md:grid-cols-1");
    if (classCount === 2) grid.addClass("md:grid-cols-2");
    if (classCount >= 3) grid.addClass("md:grid-cols-3");

  
    $("#step-3 .train-class").hide();

    availableClasses.forEach(cls => {
      $(`#step-3 .train-class[data-class="${cls}"]`).show();
    });
  


    nextStep();
      
  });





    $(document).on('click', '.changeDetailsBtn', function() {
        prevStep();
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
          <button class="px-3 py-1 rounded-md border
              ${isActive 
                  ? 'bg-primary-600 text-white border-blue-200'  
                  : 'text-gray-700 border-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800 transition-all duration-300'}">
              ${pageNumber}
          </button>
      `).on('click', () => {
          if (pageNumber !== currentPage) {
              currentPage = pageNumber;
              loadTrains(currentPage, currentKeyword,currentClassFilter);
          }
      });
    }  

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            loadTrains(currentPage, currentKeyword,currentClassFilter);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            loadTrains(currentPage, currentKeyword,currentClassFilter);
        }
    });

    $("#btnNext").on("click", () => {
        if (currentPage < totalPages) {
            currentPage += 1;
            loadTrains(currentPage, currentKeyword, currentClassFilter);
        }
    });

    $("#btnLast").on("click", () => {
        if (currentPage < totalPages) {
            currentPage = totalPages;
            loadTrains(currentPage, currentKeyword,currentClassFilter);
        }
    });

    function updateAuthUI() {
      const accessToken = localStorage.getItem('passengerAccessToken') || sessionStorage.getItem('passengerAccessToken');
      const userName = localStorage.getItem('passengerUserName') || sessionStorage.getItem('passengerUserName');
      const authContainer = $('#authContainer');
      const mobileAuthContainer = $('#mobileAuthContainer');

      if (accessToken) {
          // User is logged in - show profile dropdown
          authContainer.html(`
              <div class="relative group">
                  <button class="flex items-center space-x-2 px-4 py-2 text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg transition-all duration-300 font-medium">
                      <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                          <i class="fas fa-user text-blue-700"></i>
                      </div>
                      <span>${userName}</span>
                      <i class="fas fa-chevron-down text-xs transition-transform group-hover:rotate-180"></i>
                  </button>

                  <!-- Dropdown Menu -->
                  <div class="absolute right-0 top-full mt-2 w-48 bg-white dark:bg-gray-800 rounded-md shadow-lg py-1 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 z-50 border border-gray-200 dark:border-gray-700">
                      <div class="px-4 py-2 border-b border-gray-100 dark:border-gray-700 text-xs text-gray-500 dark:text-gray-400">
                          Signed in as <span>${userName}</span>
                      </div>
                      <a href="#" class="flex items-center px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-blue-50 dark:hover:bg-blue-900/50 hover:text-blue-700 dark:hover:text-blue-300" id="profileButton">
                          <i class="fas fa-user-circle mr-2"></i>Profile
                      </a>
                      <a href="./../pages/signin.html" class="flex items-center px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-red-50 dark:hover:bg-red-900/50 hover:text-red-700 dark:hover:text-red-300" id="logoutButton">
                          <i class="fas fa-sign-out-alt mr-2"></i>Logout
                      </a>
                  </div>
              </div>
          `);

          // Mobile view
          mobileAuthContainer.html(`
              <div class="w-full">
                  <div class="px-4 py-2 text-xs text-gray-500 dark:text-gray-400 border-b border-gray-200 dark:border-gray-700">
                      Signed in as <span class="font-medium">${userName}</span>
                  </div>
                  <a href="#" class="block py-3 text-gray-600 dark:text-gray-300 hover:text-primary-500 transition-all duration-300" id="mobileProfileButton">Profile</a>
                  <a href="#" class="block py-3 text-gray-600 dark:text-gray-300 hover:text-primary-500 transition-all duration-300" id="mobileLogoutButton">Logout</a>
              </div>
          `);

          // Add logout functionality
          $('#logoutButton, #mobileLogoutButton').on('click', function(e) {
              e.preventDefault();
              
              const logoutOverlay = $('<div>').addClass('fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50');
              const logoutModal = $('<div>').addClass('bg-white dark:bg-gray-800 rounded-lg p-6 max-w-sm mx-4 text-center transform scale-95 opacity-0 transition-all duration-300');
              
              logoutModal.html(`
                  <div class="w-16 h-16 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                  <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">Logging out</h3>
                  <p class="text-gray-600 dark:text-gray-300">Please wait while we securely sign you out...</p>
              `);
              
              logoutOverlay.append(logoutModal);
              $('body').append(logoutOverlay);
              
              setTimeout(() => {
                  logoutModal.removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
              }, 10);
              
              setTimeout(() => {
                  clearAllTokens();
                  setTimeout(() => {
                      logoutModal.removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
                      logoutOverlay.fadeOut(400, function() {
                          $(this).remove();
                          location.reload();
                      });
                  }, 1000);
              }, 800);
          });


      } else {
          // User is not logged in - show sign in/up buttons
          authContainer.html(`
              <button onclick="window.location.href='/passenger/pages/anim.html'"
              class="px-4 py-2 text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg transition-all duration-300 font-medium">
                  Sign In
              </button>
              <button onclick="window.location.href='/passenger/pages/signup.html'" 
              class="px-4 py-2 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-lg hover:shadow-lg transition-all duration-300 font-medium">
                  Sign Up
              </button>
          `);

          // Mobile view
          mobileAuthContainer.html(`
              <button onclick="window.location.href='/passenger/pages/anim.html'"
              class="flex-1 py-2 text-primary-600 bg-primary-50 dark:bg-primary-900/20 rounded-lg">
                  Sign In
              </button>
              <button onclick="window.location.href='/passenger/pages/signin.html'"
              class="flex-1 py-2 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-lg">
                  Sign Up
              </button>
          `);
      }
    }


  function setupProfileModal() {
      // Open profile modal
      $('#profileButton, #mobileProfileButton').on('click', function(e) {
          e.preventDefault();
          fetchProfileData();
          loadBookingHistory();
          $('#profileModal').removeClass('hidden');
           $('body').addClass('overflow-hidden');

          setTimeout(() => {
              $('#profileModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
          }, 10);
      });
      
      // Close profile modal
      $('#closeProfileModal').on('click', function() {
          $('#profileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
          setTimeout(() => {
              $('#profileModal').addClass('hidden');
                      $('body').removeClass('overflow-hidden');

          }, 300);
      });
      
      
      function fetchProfileData() {
          const accessToken = localStorage.getItem('passengerAccessToken') || sessionStorage.getItem('passengerAccessToken');
          const userName = localStorage.getItem('passengerUserName') || sessionStorage.getItem('passengerUserName');

          if (accessToken) {

            const requestOptions = {
              method: "GET",
              redirect: "follow"
            };

            fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/passenger/get/by/username?username=${userName}`, requestOptions)
            .then(({result}) => {
                if (result.code === 200) {
                    const profileData = result.data;
                    $('#profileTitle').text(profileData.title || "");
                    $('#profileFirstName').text(profileData.firstName || "");
                    $('#profileLastName').text(profileData.lastName || "");
                    $('#profileUsername').text(profileData.username || "");
                    $('#profileEmail').text(profileData.email || "");
                    $('#profileContact').text(profileData.phoneNumber || "");
                }
            })
            .catch((error) => console.error(error));
             
          }
        
      }
      
      // Remove the old edit functionality and connect to new modals
      $('#editProfileButton').off('click').on('click', function() {
          // Close profile modal
          $('#profileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
          setTimeout(() => {
              $('#profileModal').addClass('hidden');
              
              // Open edit profile modal after a short delay
              setTimeout(() => {
                  $('#editProfileModal').removeClass('hidden');
                  setTimeout(() => {
                      $('#editProfileModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
                  }, 10);
              }, 50);
          }, 300);
      });
      
      // Connect change password button to new modal
      $('#changePasswordButton').off('click').on('click', function() {
          // Close profile modal
          $('#profileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
          setTimeout(() => {
              $('#profileModal').addClass('hidden');
              
              // Open change password modal after a short delay
              setTimeout(() => {
                  $('#changePasswordModal').removeClass('hidden');
                  setTimeout(() => {
                      $('#changePasswordModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
                  }, 10);
              }, 50);
          }, 300);
      });
  }

  function setupEditProfileModal() {
      $('#editProfileButton').on('click', function() {
          let text = $('#profileTitle').text();
          let newText = text.slice(0, -1); 
          $('#editTitle').val(newText);
          $('#editUsername').val($('#profileUsername').text());
          $('#editFirstName').val($('#profileFirstName').text());
          $('#editLastName').val($('#profileLastName').text());
          $('#editEmail').val($('#profileEmail').text());
          $('#editContact').val($('#profileContact').text());
          
          $('#editProfileModal').removeClass('hidden');
          setTimeout(() => {
              $('#editProfileModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
          }, 10);
      });
      
    
      $('#closeEditProfileModal, #cancelEditProfile').on('click', function() {
          $('#editProfileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
          setTimeout(() => {
              $('#editProfileModal').addClass('hidden');
          }, 300);
      });
      
      $('#saveProfileChanges').on('click', function() {
          const userName = localStorage.getItem('passengerUserName') || sessionStorage.getItem('passengerUserName');

        
          const button = $(this);
          const originalText = button.html();
          
          button.html('<i class="fas fa-spinner fa-spin mr-2"></i> Saving...');
          button.prop('disabled', true);

          
  
          const raw = JSON.stringify({
            "title": $('#editTitle').val(),
            "firstName": $('#editFirstName').val(),
            "lastName": $('#editLastName').val(),
            "phoneNumber": $('#editContact').val(),
            "email": $('#editEmail').val(),
          });

           const requestOptions = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"  
            },
            body: raw,
            redirect: "follow"
    };

          fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/passenger/update/by/username?username=${userName}`, requestOptions)
            .then(({result}) => {
              console.log(result);
              
                if (result.code === 409) {
                    $("#emailError").removeClass("hidden");
                    button.html(originalText);
                    button.prop('disabled', false);
                    return;
                }
                if (result.code === 200) {
                    $("#emailError").addClass("hidden");
                       setTimeout(() => {
                          button.html('<i class="fas fa-check mr-2"></i> Saved!');
                          
                          setTimeout(() => {
                              $('#editProfileModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
                              setTimeout(() => {
                                  $('#editProfileModal').addClass('hidden');
                                  button.html(originalText);
                                  button.prop('disabled', false);
                              }, 300);
                          }, 1000);
                      }, 1500);
                }
            })
            .catch((error) => console.error(error));
      });
  }

  function setupChangePasswordModal() {
      $('#changePasswordButton').on('click', function() {
          $('#changePasswordModal').removeClass('hidden');
          setTimeout(() => {
              $('#changePasswordModal > div').removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
          }, 10);
      });
      
      $('#closeChangePasswordModal, #cancelChangePassword').on('click', function() {
          $('#changePasswordModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
          setTimeout(() => {
              $('#changePasswordModal').addClass('hidden');
              $('#changePasswordForm')[0].reset();
          }, 300);
          $('#passwordError').addClass('hidden');
      });
      
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

          const button = $(this);
          const originalText = button.html();
          
          // Get form values
          const currentPassword = $('#currentPassword').val();
          const newPassword = $('#newPassword').val();
          const confirmPassword = $('#confirmPassword').val();

          
          // Simple validation
          if (!currentPassword || !newPassword || !confirmPassword) {
              showError('Please fill in all password fields');
              return;
          }
          
          if (newPassword !== confirmPassword) {
              showError('New passwords do not match');
              return;
          }
          
          if (newPassword.length < 5) {
              showError('Password must be at least 8 characters long');
              return;
          }
          $('#passwordError').addClass('hidden');
          
          const userName = localStorage.getItem('passengerUserName') || sessionStorage.getItem('passengerUserName');


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

          fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/passenger/change/password", requestOptions)
            .then(({result}) => {
              console.log(result);
              if (result.code ===200) {
                if (!result.data) {
                  showError('Current password is incorrect');
                  return;
                }
                button.html('<i class="fas fa-spinner fa-spin mr-2"></i> Updating...');
                button.prop('disabled', true);
          
                setTimeout(() => {
                    // Show success message
                    button.html('<i class="fas fa-check mr-2"></i> Updated!');
                    
                    // Close modal and reset form after delay
                    setTimeout(() => {
                        $('#changePasswordModal > div').removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
                        setTimeout(() => {
                            $('#changePasswordModal').addClass('hidden');
                            $('#changePasswordForm')[0].reset();
                            button.html(originalText);
                            button.prop('disabled', false);
                            showAlert('Password updated successfully!', 'success');
                            logoutModel();
                        }, 300);
                    }, 1000);
                }, 1500);
              }

            })
            .catch((error) => console.error(error));
                    
      });
  }

   function showError(message) {
        $("#passwordError").text(message).removeClass("hidden");
        $("#passwordError").addClass("animate__animated animate__headShake");
        setTimeout(() => {
        $("#passwordError").removeClass("animate__headShake");
        }, 1000);
    }

    function logoutModel(){
      const logoutOverlay = $('<div>').addClass('fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50');
              const logoutModal = $('<div>').addClass('bg-white dark:bg-gray-800 rounded-lg p-6 max-w-sm mx-4 text-center transform scale-95 opacity-0 transition-all duration-300');
              
              logoutModal.html(`
                  <div class="w-16 h-16 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                  <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">Logging out</h3>
                  <p class="text-gray-600 dark:text-gray-300">Please wait while we securely sign you out...</p>
              `);
              
              logoutOverlay.append(logoutModal);
              $('body').append(logoutOverlay);
              
              setTimeout(() => {
                  logoutModal.removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
              }, 10);
              
              setTimeout(() => {
                  clearAllTokens();
                  setTimeout(() => {
                      logoutModal.removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
                      logoutOverlay.fadeOut(400, function() {
                          $(this).remove();
                          window.location.href='/passenger/pages/anim.html';
                      });
                  }, 1000);
              }, 800);
    }

    function showAlert(message, type = 'error') {
      const alert = $('#global-alert');
      const alertContent = alert.find('.rounded-lg');
      const alertMessage = alert.find('.alert-message');
      const alertIcon = alert.find('.alert-icon');
      
      // Remove all previous alert classes
      alertContent.removeClass('alert-error alert-success alert-warning alert-info');
      
      // Add the appropriate class based on type
      alertContent.addClass(`alert-${type}`);
      
      // Set the message
      alertMessage.text(message);
      
      // Show the alert with animation
      alert.removeClass('hidden').addClass('animate__animated animate__fadeInDown');
      
      // Auto hide after 5 seconds
      setTimeout(() => {
          hideAlert();
      }, 5000);
      
      // Close button functionality
      $('.alert-close').off('click').on('click', function() {
          hideAlert();
      });
    }

    function hideAlert() {
      const alert = $('#global-alert');
      alert.removeClass('animate__fadeInDown').addClass('animate__fadeOutUp');
      
      setTimeout(() => {
          alert.addClass('hidden').removeClass('animate__fadeOutUp');
      }, 300);
    }

    $(document).on("click","#btnBookingYourFirstJourney", function() {
        $('#profileModal').addClass('hidden');
        $('body').removeClass('overflow-hidden');
        $('html, body').animate({
            scrollTop: $("#booking").offset().top - 20
        }, 500);
        $("#departureStation").focus();
    });

    function loadBookingHistory() {
      const userName = localStorage.getItem('passengerUserName') || sessionStorage.getItem('passengerUserName');

      const requestOptions = {
        method: "GET",
        redirect: "follow"
      };

      fetchWithTokenRefresh(`http://localhost:8080/api/v1/raillankapro/passenger/get/bookings?username=${userName}`, requestOptions)
        .then(({result}) => {
          console.log(result);
          
            if (result.code === 200) {
              const bookings = result.data;
              const historyContainer = $("#bookingHistory");
              historyContainer.empty();

              if (bookings.length != 0) {
                bookings.forEach(booking => {
                  const status = booking.status.toUpperCase();
                  let colorTheme;
                  let icon;
                  if (status !== "COMPLETED") {
                    colorTheme = "green"
                    icon = "fa-check-circle"
                  }else if (status === "UPCOMING") {
                    colorTheme = "blue"
                    icon = "fa-arrow-right"
                  }else if (status === "TODAY") {
                    icon = "fa-clock"
                    colorTheme = "rose"
                  }
                  historyContainer.append(`
                    <div
                      class="bg-white dark:bg-gray-600 p-4 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-300"
                    >
                      <div class="flex justify-between items-start mb-3">
                        <div>
                          <span
                            class="font-semibold text-gray-800 dark:text-white"
                            >${booking.departureStation} to ${booking.destinationStation}</span
                          >
                          <p
                            class="text-xs text-gray-500 dark:text-gray-300 mt-1"
                          >
                          ${booking.trainType} • ${booking.travelClass}
                          </p>
                        </div>
                        <span
                          class="text-xs font-medium bg-${colorTheme}-100 text-${colorTheme}-800 dark:bg-${colorTheme}-800 dark:text-${colorTheme}-100 px-3 py-1.5 rounded-full flex items-center"
                        >
                          <i class="fas ${icon} mr-1.5"></i> Completed
                        </span>
                      </div>
                      <div class="grid grid-cols-2 gap-3 text-sm">
                        <div
                          class="flex items-center text-gray-600 dark:text-gray-300"
                        >
                          <i
                            class="far fa-calendar-alt mr-2 text-primary-600"
                          ></i>
                          <span>${booking.bookingDate}</span>
                        </div>
                        <div
                          class="flex items-center text-gray-600 dark:text-gray-300"
                        >
                          <i class="far fa-clock mr-2 text-primary-600"></i>
                          <span>${booking.bookingTime}</span>
                        </div>
                      </div>
                      <div
                        class="mt-3 pt-3 border-t border-gray-100 dark:border-gray-700"
                      >
                        <p class="text-xs text-gray-500 dark:text-gray-400">
                          Booking ID: ${booking.bookingId}
                        </p>
                      </div>
                    </div>
                  `);
                });
              }else {
                 historyContainer.html(`
                  <div class="text-center py-12 px-4">
                      <div class="w-24 h-24 mx-auto mb-6 bg-blue-100 dark:bg-blue-900/30 rounded-full flex items-center justify-center">
                          <i class="fas fa-ticket-alt text-3xl text-blue-600 dark:text-blue-400"></i>
                      </div>
                      <h3 class="text-xl font-semibold text-gray-800 dark:text-white mb-2">No Bookings Yet</h3>
                      <p class="text-gray-600 dark:text-gray-300 mb-6 max-w-md mx-auto">
                          You haven't made any bookings yet. Start your journey with RailLanka Pro and explore beautiful destinations across Sri Lanka.
                      </p>
                      <button id="btnBookingYourFirstJourney" class="px-6 py-3 bg-gradient-to-r from-primary-600 to-primary-700 hover:from-primary-700 hover:to-primary-800 text-white rounded-xl font-medium transition-all duration-300 transform hover:-translate-y-0.5 hover:shadow-lg">
                          <i class="fas fa-train mr-2"></i>Book Your First Journey
                      </button>
                  </div>
              `);
              }
            }
        })
        .catch((error) => console.error(error));
    }


//     function handlePaymentProcess() {
//     // Proceed to payment button click
//     $('#proceed-to-payment').on('click', function() {
//         // Simulate payment processing (replace with actual payment gateway integration)
//         simulatePaymentProcessing();
//     });
// }

// // Function to simulate payment processing
// function simulatePaymentProcessing() {
//     // Show processing overlay
//     const processingOverlay = $('<div>').addClass('fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50');
//     const processingModal = $('<div>').addClass('bg-white rounded-lg p-6 max-w-sm mx-4 text-center transform scale-95 opacity-0 transition-all duration-300 shadow-xl');
    
//     processingModal.html(`
//         <div class="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
//         <h3 class="text-lg font-semibold text-gray-800 mb-2">Processing Payment</h3>
//         <p class="text-gray-600">Redirecting to secure payment gateway...</p>
//     `);
    
//     processingOverlay.append(processingModal);
//     $('body').append(processingOverlay);
    
//     // Animate modal in
//     setTimeout(() => {
//         processingModal.removeClass('scale-95 opacity-0').addClass('scale-100 opacity-100');
//     }, 10);
    
//     // Simulate payment processing delay
//     setTimeout(() => {
//         // Simulate successful payment
//         processingModal.html(`
//             <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
//                 <i class="fas fa-check text-green-600 text-2xl"></i>
//             </div>
//             <h3 class="text-lg font-semibold text-gray-800 mb-2">Payment Successful</h3>
//             <p class="text-gray-600">Your payment has been processed successfully.</p>
//         `);
        
//         // After a brief delay, close the modal and show payment confirmation
//         setTimeout(() => {
//             processingModal.removeClass('scale-100 opacity-100').addClass('scale-95 opacity-0');
//             processingOverlay.fadeOut(400, function() {
//                 $(this).remove();
                
//                 // Hide before payment section, show after payment section
//                 $('#before-payment').addClass('hidden');
//                 $('#after-payment').removeClass('hidden');
                
//                 // Generate a random transaction ID (in real app, this would come from payment gateway)
//                 const transactionId = 'TXN-' + Math.floor(100000 + Math.random() * 900000);
//                 $('#transaction-id').text(transactionId);
                
//                 // Set current date and time
//                 const now = new Date();
//                 const options = { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' };
//                 $('#payment-date').text(now.toLocaleDateString('en-US', options));
                
//                 // Enable confirm booking button if needed
//                 $('#confirm-booking').prop('disabled', false);
//             });
//         }, 1500);
//     }, 2500);
// }


});


