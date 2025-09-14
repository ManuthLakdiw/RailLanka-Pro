
$(document).ready(function () {

    toastr.options = {
        closeButton: true,
        progressBar: true,
        positionClass: "toast-top-right",
        timeOut: 2000,
    };

    let token = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');

    if (token) {
        let userName = localStorage.getItem('userName') || sessionStorage.getItem('userName');
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

    $("#adminEmail").text(localStorage.getItem('email'));

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

    async function fetchWithTokenRefresh(url, options = {}, responseType = 'json') {
        // Get token
        let accessToken = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');
        
        if (!accessToken) {
            console.log("❌ No access token found. Redirecting...");
            window.location.href = "../../logging-expired.html";
            return;
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
            let result;

            // Check response type
            if (responseType === 'json') {
                result = await response.json();
            } else if (responseType === 'blob') {
                result = await response.blob();
            } else if (responseType === 'text') {
                result = await response.text();
            }

            console.log("📥 API response:", result);

            // Handle expired token for JSON responses
            if (responseType === 'json' && result.code === 401 && result.message === "JWT token has expired") {
                console.log('⚠️ Access token expired, attempting to refresh...');
                
                const newToken = await refreshAccessToken();
                
                if (newToken) {
                    console.log("🔄 Retrying API call with new token:", newToken);
                    const retryHeaders = {
                        ...options.headers,
                        'Authorization': `Bearer ${newToken}`
                    };
                    
                    const retryOptions = {
                        ...options,
                        headers: retryHeaders
                    };
                    
                    response = await fetch(url, retryOptions);

                    if (responseType === 'json') result = await response.json();
                    else if (responseType === 'blob') result = await response.blob();
                    else if (responseType === 'text') result = await response.text();

                    console.log("📥 Retried API response:", result);
                }
            }

            return { response, result };
        } catch (error) {
            console.error('🔥 Fetch error:', error);
            throw error;
        }
    }



    loadTrainDetailsForCard();
    loadTrainCategoryChart();
    loadTrainStatusChart();

    loadStationDetailsForCard();
    loadStationProvinceChart();
    loadStationStatusChart();

    loadScheduleDetailsForCard();
    loadScheduleFrequencyChart();
    loadScheduleStatusChart();

    loadSMasterDetailsForCard();
    loadSMasterProvinceChart();
    loadSMasterStatusChart();

    loadCounterDetailsForCard();
    loadCounterProvinceChart();
    loadCounterStatusChart();

    loadEmployeeDetailsForCard();
    loadEmployeeCategoryChart();
    loadEmployeeStatusChart();


    $(".filter-btn").on("click", function () {
      const parent = $(this).closest(".flex");

      parent.find(".filter-btn")
        .removeClass("active bg-blue-100 text-blue-700")
        .addClass("bg-gray-100 text-gray-700");

      $(this)
        .addClass("active bg-blue-100 text-blue-700")
        .removeClass("bg-gray-100 text-gray-700");

    });


    $("#trainExportBtn").on("click", function () {
        const activeFilter = $("#train-report .filter-btn.active").data("filter");
        console.log("Active Filter:", activeFilter);

        if (activeFilter === "all") {
          generatePdf("/all/trains");
        }else if (activeFilter === "active") {
          generatePdf("/active/trains");
        }else if (activeFilter === "inactive") {
          generatePdf("/inactive/trains");
        }
        
    });

    $("#stationExportBtn").on("click", function () {
        const activeFilter = $("#station-report .filter-btn.active").data("filter");
        console.log("Active Filter:", activeFilter);

        if (activeFilter === "all") {
          generatePdf("/all/stations");
        }else if (activeFilter === "active") {
          generatePdf("/active/stations");
        }else if (activeFilter === "inactive") {
          generatePdf("/inactive/stations");
        }
        
    });


    $("#scheduleExportBtn").on("click", function () {
      const activeFilter = $("#schedule-report .filter-btn.active").data("filter");
      console.log("Active Filter:", activeFilter);

      if (activeFilter === "all") {
        generatePdf("/all/schedules");
      }else if (activeFilter === "active") {
        generatePdf("/active/schedules");
      }else if (activeFilter === "inactive") {
        generatePdf("/inactive/schedules");
      }
        
    });


    $("#sMasterExportBtn").on("click", function () {
      const activeFilter = $("#stationmaster-report .filter-btn.active").data("filter");
      console.log("Active Filter:", activeFilter);

      if (activeFilter === "all") {
        generatePdf("/all/stationmasters");
      }else if (activeFilter === "active") {
        generatePdf("/active/stationmasters");
      }else if (activeFilter === "inactive") {
        generatePdf("/inactive/stationmasters");
      }
        
    });


    $("#counterExportBtn").on("click", function () {
      const activeFilter = $("#counter-report .filter-btn.active").data("filter");
      console.log("Active Filter:", activeFilter);

      if (activeFilter === "all") {
        generatePdf("/all/counters");
      }else if (activeFilter === "active") {
        generatePdf("/active/counters");
      }else if (activeFilter === "inactive") {
        generatePdf("/inactive/counters");
      }
        
    });

      $("#employeeExportBtn").on("click", function () {
      const activeFilter = $("#employee-report .filter-btn.active").data("filter");
      console.log("Active Filter:", activeFilter);

      if (activeFilter === "all") {
        generatePdf("/all/employees");
      }else if (activeFilter === "active") {
        generatePdf("/active/employees");
      }else if (activeFilter === "inactive") {
        generatePdf("/inactive/employees");
      }
        
    });


    async function generatePdf(url = null) {
      let startUrl = "http://localhost:8080/api/v1/raillankapro/pdf/download"
      if (url) {
          toastr.info("Generating PDF report...");
          
          try {
              const { result: blob } = await fetchWithTokenRefresh(
                  `${startUrl}${url}`, 
                  { method: "GET" }, 
                  'blob'
              );
              
              let fileName = url.replace(/\//g, "_").replace(/^_/, "");
              const downloadUrl = window.URL.createObjectURL(blob);
              const $a = $('<a />', {
                  href: downloadUrl,
                  download: fileName + "_" + Date.now() + ".pdf"
              }).appendTo("body");
              
              $a[0].click();
              $a.remove();
              window.URL.revokeObjectURL(downloadUrl);
              toastr.success("PDF report downloaded successfully");
          } catch (error) {
              console.error(error);
              toastr.error("Failed to download PDF. Please try again.");
          }
      } else {
          toastr.error("URL Null!!!");
      }
    }

    function animateNumber(id, target, duration = 1000) {
      let start = 0;
      let stepTime = Math.abs(Math.floor(duration / target));
      let element = document.getElementById(id);

      let timer = setInterval(function () {
        start++;
        element.textContent = start;
        if (start >= target) {
          clearInterval(timer);
        }
      }, stepTime);
    }

    $(".report-tab").on("click", function () {
      const targetTab = $(this).data("tab");

      $(".report-tab").removeClass("active");
      $(this).addClass("active");

      $(".report-content").each(function () {
        if ($(this).attr("id") === targetTab) {
          $(this).removeClass("hidden");

          if (targetTab === "train-report") {
            loadTrainDetailsForCard();
            loadTrainCategoryChart();
            loadTrainStatusChart();
          } else if (targetTab === "station-report") {
            loadStationDetailsForCard();
            loadStationProvinceChart();
            loadStationStatusChart();
          } else if (targetTab === "schedule-report") {
            loadScheduleDetailsForCard();
            loadScheduleFrequencyChart();
            loadScheduleStatusChart();
          } else if (targetTab === "stationmaster-report") {
            loadSMasterDetailsForCard();
            loadSMasterProvinceChart();
            loadSMasterStatusChart();
          } else if (targetTab === "counter-report") {
            loadCounterDetailsForCard();
            loadCounterProvinceChart();
            loadCounterStatusChart();
          } else if (targetTab === "employee-report") {
            loadEmployeeDetailsForCard();
            loadEmployeeCategoryChart();
            loadEmployeeStatusChart();
          }

        } else {
          $(this).addClass("hidden");
        }
      });
    });




    ///////////////////////// train report /////////////////////////

    function loadTrainDetailsForCard(){

        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/train/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const data = result.data;
      

            animateNumber("totalTrains", data.total, 1000);
            animateNumber("activeTrains", data.active, 1000);
            animateNumber("inactiveTrains", data.inactive, 1000);

            const percentage = (data.active / data.total) * 100;

            $("#trainProgressBar")
            .stop()                 
            .css("width", 0)        
            .animate(
              { width: percentage + "%" },
              1000                  
            );
        })
        .catch((error) => console.error(error));
        
        animateNumber("trainCategoryCount", 4, 1000);
        animateNumber("trainTypeCount", 4, 1000);



    }

    function loadTrainCategoryChart(){
        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/train/count/by/type", requestOptions)
        .then(({result}) => {
            console.log(result)
            const counts = result.data;

            const labels = ["EXPRESS", "NORMAL", "INTERCITY", "SPECIAL"];

             const formattedLabels = labels.map(
                (label) => label.charAt(0).toUpperCase() + label.slice(1).toLowerCase()
            );

            const data = labels.map((label) => counts[label] || 0);

            trainCategoryChart.data.labels = formattedLabels;
            trainCategoryChart.data.datasets[0].data = data;
            trainCategoryChart.update();
        })
        .catch((error) => console.error(error));

    }

    function loadTrainStatusChart(){
        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/train/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const counts = result.data;
     
            const data = [counts.active, counts.inactive];
            trainStatusChart.data.datasets[0].data = data;
            trainStatusChart.update();
        })
        .catch((error) => console.error(error));

    }

    const trainCategoryChart = new Chart($("#trainTypeChart"), {
    type: "bar",
    data: {
        labels: [], // start empty
        datasets: [
        {
            label: "Number of Trains",
            data: [],
            backgroundColor: [
            "rgba(54, 162, 235, 0.7)",
            "rgba(75, 192, 192, 0.7)",
            "rgba(255, 206, 86, 0.7)",
            "rgba(153, 102, 255, 0.7)",
            ],
            borderColor: [
            "rgba(54, 162, 235, 1)",
            "rgba(75, 192, 192, 1)",
            "rgba(255, 206, 86, 1)",
            "rgba(153, 102, 255, 1)",
            ],
            borderWidth: 1,
        },
        ],
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true } },
    },
    });

    const trainStatusChart = new Chart($("#trainStatusChart"), {
      type: "doughnut",
      data: {
        labels: ["Active", "Inactive"],
        datasets: [
          {
            data: [],
            backgroundColor: [
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 99, 132, 0.7)",
            ],
            borderColor: [
              "rgba(75, 192, 192, 1)",
              "rgba(255, 99, 132, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: "bottom" } },
      },
    });

    

    ///////////////////////// station report /////////////////////////


    function loadStationDetailsForCard(){

      const requestOptions = {
      method: "GET",
      redirect: "follow"
      };

      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/station/count", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;
    

          animateNumber("totalStations", data.total, 1000);
          animateNumber("activeStations", data.inService, 1000);
          animateNumber("inactiveStations", data.outService, 1000);

          const percentage = (data.inService / data.total) * 100;

          $("#stationProgressBar")
          .stop()                 
          .css("width", 0)        
          .animate(
            { width: percentage + "%" },
            1000                  
          );
    })
    .catch((error) => console.error(error));
    
    animateNumber("stationProvinceCount", 9, 1000);
    animateNumber("trainTypeCount", 4, 1000);
    animateNumber("stationAvg", 42, 1000);



    }   

    function loadStationProvinceChart() {
      const requestOptions = {
          method: "GET",
          redirect: "follow"
      };

      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/station/count/by/province", requestOptions)
          .then(({ result }) => {
              console.log(result);
              const counts = result.data;

              const labels = [
                  "Western",
                  "Central",
                  "Southern",
                  "Northern",
                  "Eastern",
                  "North Western",
                  "North Central",
                  "Uva",
                  "Sabaragamuwa"
              ];

              const data = labels.map(label => {
                  const entry = counts.find(c => c.province.toLowerCase().includes(label.toLowerCase()));
                  return entry ? entry.stationCount : 0;
              });

              stationProvinceChart.data.labels = labels;
              stationProvinceChart.data.datasets[0].data = data;
              stationProvinceChart.update();
          })
          .catch((error) => console.error(error));
    }

    function loadStationStatusChart(){
        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/station/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const data = result.data;
      

            const counts = [data.inService, data.outService];
            stationStatusChart.data.datasets[0].data = counts;
            stationStatusChart.update();
      })
      .catch((error) => console.error(error));
    }

    const stationProvinceChart = new Chart($("#stationProvinceChart"), {
      type: "bar",
      data: {
        labels: [
          "Western",
          "Central",
          "Southern",
          "Northern",
          "Eastern",
          "North Western",
          "North Central",
          "Uva",
          "Sabaragamuwa",
        ],
        datasets: [
          {
            label: "Number of Stations",
            data: [],
            backgroundColor: "rgba(54, 162, 235, 0.7)",
            borderColor: "rgba(54, 162, 235, 1)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true } },
      },
    });

    const stationStatusChart = new Chart($("#stationStatusChart"), {
      type: "doughnut",
      data: {
        labels: ["Active", "Inactive"],
        datasets: [
          {
            data: [],
            backgroundColor: [
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 99, 132, 0.7)",
            ],
            borderColor: [
              "rgba(75, 192, 192, 1)",
              "rgba(255, 99, 132, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
       options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: "bottom" } },
      },
    });





    ///////////////////////// schedule report  /////////////////////////

    function loadScheduleDetailsForCard(){

      const requestOptions = {
      method: "GET",
      redirect: "follow"
      };

      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/schedule/count", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;
    

          animateNumber("totalSchedules", data.total, 1000);
          animateNumber("activeSchedules", data.active, 1000);
          animateNumber("inactiveSchedules", data.inactive, 1000);

          const percentage = (data.active / data.total) * 100;

          $("#scheduleProgressBar")
          .stop()                 
          .css("width", 0)        
          .animate(
            { width: percentage + "%" },
            1000                  
          );
      })
      .catch((error) => console.error(error));


      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/schedule/avg/daily", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;

          animateNumber("scheduleAvgDaily", data, 1000);
      
      })
      .catch((error) => console.error(error));


      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/train/count/by/schedule", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;

          animateNumber("scheduledtrainCount", data.trainsWithSchedule, 1000);
          animateNumber("totalTrainCountWithoutSchedule", data.totalTrains, 1000);
      
      })
      .catch((error) => console.error(error));


    


    }  
    
    function loadScheduleFrequencyChart() {
        const requestOptions = {
            method: "GET",
            redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/schedule/count/by/frequencies", requestOptions)
            .then(({ result }) => {
                console.log(result);
                const data = result.data;

                const labels = ["Daily", "Weekdays", "Weekends", "Custom"];
                const dataSet = [
                    data.DAILY || 0,
                    data.WEEK_DAYS || 0,
                    data.WEEK_ENDS || 0,
                    data.CUSTOM || 0
                ];

                scheduleFrequencyChart.data.datasets[0].data = dataSet;
                scheduleFrequencyChart.update();
            })
            .catch((error) => console.error(error));
    }

    function loadScheduleStatusChart(){
      const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/schedule/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const data = result.data;
      

            const counts = [data.active, data.inactive];
            scheduleStatusChart.data.datasets[0].data = counts;
            scheduleStatusChart.update();
      })
      .catch((error) => console.error(error));
    }

    const scheduleFrequencyChart = new Chart($("#scheduleFrequencyChart"), {
      type: "bar",
      data: {
        labels: ["Daily", "Weekdays", "Weekends", "Custom"],
        datasets: [
          {
            label: "Number of Schedules",
            data: [],
            backgroundColor: [
              "rgba(54, 162, 235, 0.7)",
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 206, 86, 0.7)",
              "rgba(153, 102, 255, 0.7)",
            ],
            borderColor: [
              "rgba(54, 162, 235, 1)",
              "rgba(75, 192, 192, 1)",
              "rgba(255, 206, 86, 1)",
              "rgba(153, 102, 255, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true } },
      },
    });

    const scheduleStatusChart = new Chart($("#scheduleStatusChart"), {
      type: "doughnut",
      data: {
        labels: ["Active", "Inactive"],
        datasets: [
          {
            data: [295, 33],
            backgroundColor: [
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 99, 132, 0.7)",
            ],
            borderColor: [
              "rgba(75, 192, 192, 1)",
              "rgba(255, 99, 132, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: "bottom" } },
      },
    });



    ///////////////////////// station master report  /////////////////////////

    function loadSMasterDetailsForCard(){
      const requestOptions = {
      method: "GET",
      redirect: "follow"
      };

      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/stationmaster/count", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;
    

          animateNumber("totalSMasters", data.total, 1000);
          animateNumber("activeSMasters", data.active, 1000);
          animateNumber("inacticeSMasters", data.inactive, 1000);

          const percentage = (data.active / data.total) * 100;

          $("#SmasterProgressBar")
          .stop()                 
          .css("width", 0)        
          .animate(
            { width: percentage + "%" },
            1000                  
          );
      })
      .catch((error) => console.error(error));


      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/station/total/and/stationmaster/count", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;
    

          animateNumber("sMasterTotalStations", data.total, 1000);
          animateNumber("sMasterCoveredStations", data.assigned, 1000);
      
      })
      .catch((error) => console.error(error));


      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/stationmaster/avg/experience", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;
    

          animateNumber("sMasterAvgExperience", data, 1000);
      
      })
      .catch((error) => console.error(error));
    }

    function loadSMasterProvinceChart() {
      const requestOptions = {
        method: "GET",
        redirect: "follow",
      };

      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/stationmaster/count/by/province", requestOptions)
        .then(({ result }) => {
          console.log(result);
          const counts = result.data;

          const labels = [
            "Western",
            "Central",
            "Southern",
            "Northern",
            "Eastern",
            "North Western",
            "North Central",
            "Uva",
            "Sabaragamuwa",
          ];

          const data = labels.map((label) => {
            const key = Object.keys(counts).find((k) =>
              k.toLowerCase().includes(label.toLowerCase())
            );
            return key ? counts[key] : 0;
          });

          sMasterProvinceChart.data.labels = labels;
          sMasterProvinceChart.data.datasets[0].data = data;
          sMasterProvinceChart.update();
        })
        .catch((error) => console.error(error));
    }

    function loadSMasterStatusChart(){
        const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/stationmaster/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const data = result.data;
      

            const counts = [data.active, data.inactive];
            sMasterStatusChart.data.datasets[0].data = counts;
            sMasterStatusChart.update();
      })
      .catch((error) => console.error(error));
    }

    const sMasterStatusChart = new Chart($("#stationMasterStatusChart"), {
      type: "doughnut",
      data: {
        labels: ["Active", "Inactive"],
        datasets: [
          {
            data: [295, 33],
            backgroundColor: [
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 99, 132, 0.7)",
            ],
            borderColor: [
              "rgba(75, 192, 192, 1)",
              "rgba(255, 99, 132, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: "bottom" } },
      },
    });

    const sMasterProvinceChart = new Chart($("#stationMasterProvinceChart"), {
      type: "radar",
      data: {
        labels: [
          "Western",
          "Central",
          "Southern",
          "Northern",
          "Eastern",
          "North Western",
          "North Central",
          "Uva",
          "Sabaragamuwa",
        ],
        datasets: [
          {
            label: "Number of Station Masters",
            data: [],
            backgroundColor: "rgba(54, 162, 235, 0.7)",
            borderColor: "rgba(54, 162, 235, 1)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true } },
      },
    });


    

    ///////////////////////// counter report  /////////////////////////
    
    function loadCounterDetailsForCard(){
      const requestOptions = {
      method: "GET",
      redirect: "follow"
      };

      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/counter/count", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;
    

          animateNumber("totalCounterStaff", data.total, 1000);
          animateNumber("activeCounterStaff", data.active, 1000);
          animateNumber("inactiveCounterStaff", data.inactive, 1000);

          const percentage = (data.active / data.total) * 100;

          $("#CounterProgressBar")
          .stop()                 
          .css("width", 0)        
          .animate(
            { width: percentage + "%" },
            1000                  
          );
      })
      .catch((error) => console.error(error));



      fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/station/count", requestOptions)
      .then(({result}) => {
          console.log(result)
          const data = result.data;

          let totalCounterMachinesInStations = data.total * 3; 

        animateNumber("totalCounterStations", data.total, 1000);
        animateNumber("totalCounterMachines", totalCounterMachinesInStations, 1000);

          
        })
       .catch((error) => console.error(error));

      animateNumber("stationWithCounters", 3, 1000);
      animateNumber("stationPerCounter", 3, 1000);


    }

    function loadCounterProvinceChart() {
      const requestOptions = {
        method: "GET",
        redirect: "follow",
      };

      fetchWithTokenRefresh(
        "http://localhost:8080/api/v1/raillankapro/counter/count/by/province",
        requestOptions
      )
        .then(({ result }) => {
          console.log(result);
          const counts = result.data; // <-- this is an array

          const labels = [
            "Western",
            "Central",
            "Southern",
            "Northern",
            "Eastern",
            "North Western",
            "North Central",
            "Uva",
            "Sabaragamuwa",
          ];

          const data = labels.map((label) => {
            const entry = counts.find((c) =>
              c.province.toLowerCase().includes(label.toLowerCase())
            );
            return entry ? entry.counterStaffCount : 0;
          });

          counterProvinceChart.data.labels = labels;
          counterProvinceChart.data.datasets[0].data = data;
          counterProvinceChart.update();
        })
        .catch((error) => console.error(error));
    }
  

    function loadCounterStatusChart(){
      const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/counter/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const data = result.data;
      

            const counts = [data.active, data.inactive];
            counterStatusChart.data.datasets[0].data = counts;
            counterStatusChart.update();
      })
      .catch((error) => console.error(error));
    }


    const counterStatusChart = new Chart($("#counterStatusChart"), {
      type: "doughnut",
      data: {
        labels: ["Active", "Inactive"],
        datasets: [
          {
            data: [295, 33],
            backgroundColor: [
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 99, 132, 0.7)",
            ],
            borderColor: [
              "rgba(75, 192, 192, 1)",
              "rgba(255, 99, 132, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: "bottom" } },
      },
    });

    const counterProvinceChart = new Chart($("#counterProvinceChart"), {
      type: "bar",
      data: {
        labels: [
          "Western",
          "Central",
          "Southern",
          "Northern",
          "Eastern",
          "North Western",
          "North Central",
          "Uva",
          "Sabaragamuwa",
        ],
        datasets: [
          {
            label: "Number of Counters",
            data: [],
            backgroundColor: "rgba(54, 162, 235, 0.7)",
            borderColor: "rgba(54, 162, 235, 1)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true } },
      },
    });






    ///////////////////////// employee report  /////////////////////////

    async function loadEmployeeDetailsForCard() {
        const requestOptions = {
            method: "GET",
            redirect: "follow"
        };

        try {
            const employeeResponse = await fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/employee/all/count", requestOptions);
            const employeeData = employeeResponse.result.data;

            const totalEmployees = employeeData.total;

            animateNumber("totalEmployees", employeeData.total, 1000);
            animateNumber("activeEmployees", employeeData.active, 1000);
            animateNumber("inactiveEmployees", employeeData.inactive, 1000);

            const empPercentage = (employeeData.active / employeeData.total) * 100;
            $("#employeeProgressBar").stop().css("width", 0).animate({ width: empPercentage + "%" }, 1000);

            const smResponse = await fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/stationmaster/count", requestOptions);
            const smData = smResponse.result.data;

            const smPercentage = (smData.total / totalEmployees) * 100;
            animateNumber("empSMasterTotal", smData.total, 1000);
            animateNumber("empSmasterPrecentage", smPercentage.toFixed(2), 1000);

            const counterResponse = await fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/counter/count", requestOptions);
            const counterData = counterResponse.result.data;

            const counterPercentage = (counterData.total / totalEmployees) * 100;
            animateNumber("empCounterTotal", counterData.total, 1000);
            animateNumber("empCounterPrecentage", counterPercentage.toFixed(2), 1000);

        } catch (error) {
            console.error(error);
        }
    }

    function loadEmployeeCategoryChart(){
        const requestOptions = {
            method: "GET",
            redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/employee/all/count/by/position", requestOptions)
        .then(({result}) => {
            console.log(result);
            const counts = result.data;

            const labels = Object.keys(counts); // fix keys()
            const formattedLabels = labels.map(
                label => label
                    .toLowerCase()
                    .split('_')
                    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(' ')
            );

            const data = labels.map(label => counts[label] || 0);

            employeeCategoryChart.data.labels = formattedLabels;
            employeeCategoryChart.data.datasets[0].data = data;
            employeeCategoryChart.update(); // fix chart update
        })
        .catch((error) => console.error(error));
    }

    function loadEmployeeStatusChart(){
      const requestOptions = {
        method: "GET",
        redirect: "follow"
        };

        fetchWithTokenRefresh("http://localhost:8080/api/v1/raillankapro/employee/all/count", requestOptions)
        .then(({result}) => {
            console.log(result)
            const data = result.data;
      

            const counts = [data.active, data.inactive];
            employeeStatusChart.data.datasets[0].data = counts;
            employeeStatusChart.update();
      })
      .catch((error) => console.error(error));
    }

    const employeeCategoryChart = new Chart(document.getElementById('employeeRoleChart'), {
      type: "bar",
      data: {
        labels: [],
        datasets: [{
          label: "Number of Employees",
          data: [],
          backgroundColor: [
            "rgba(54, 162, 235, 0.7)",
            "rgba(75, 192, 192, 0.7)",
            "rgba(255, 206, 86, 0.7)",
            "rgba(153, 102, 255, 0.7)",
            "rgba(255, 99, 132, 0.7)",
            "rgba(255, 159, 64, 0.7)",
            "rgba(201, 203, 207, 0.7)",
            "rgba(100, 149, 237, 0.7)"
          ],
          borderColor: [
            "rgba(54, 162, 235, 1)",
            "rgba(75, 192, 192, 1)",
            "rgba(255, 206, 86, 1)",
            "rgba(153, 102, 255, 1)",
            "rgba(255, 99, 132, 1)",
            "rgba(255, 159, 64, 1)",
            "rgba(201, 203, 207, 1)",
            "rgba(100, 149, 237, 1)"
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        
      }
    });

    const employeeStatusChart = new Chart($("#employeeStatusChart"), {
      type: "doughnut",
      data: {
        labels: ["Active", "Inactive"],
        datasets: [
          {
            data: [1,24],
            backgroundColor: [
              "rgba(75, 192, 192, 0.7)",
              "rgba(255, 99, 132, 0.7)",
            ],
            borderColor: [
              "rgba(75, 192, 192, 1)",
              "rgba(255, 99, 132, 1)",
            ],
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: "bottom" } },
      },
    });


});

