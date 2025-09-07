$(document).ready(function () {
    // Toastr options
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
    let status;
    let passengerType;

    let selectedBtn = null; 
   

    fetchPassengers(currentPage)

    function openModal(modalId) {
        $("#" + modalId).addClass("active");
    }

    function closeModal(modalId) {
        $("#" + modalId).removeClass("active");
    }

    $(".view-btn").click(function () {
        openModal("passengerModal");
    });


    $("#closeModal, #closeViewBtn").click(function () {
        closeModal("passengerModal");
    });

    $("#closeBlockModal, #cancelBlockBtn").click(function () {
        closeModal("blockModal");
    });

    // Export PDF functionality
    $("#exportPdfBtn").click(function () {
        // toastr.info("Generating PDF report...");
        // // In a real application, you would make an API call to generate a PDF
        // setTimeout(() => {
        // toastr.success("PDF report downloaded successfully");
        // }, 1500);
        // openModal("exportPdfModal")
    });

    $(document).on("click", ".export-option", function() {
        // data attribute ganna
        const reportType = $(this).data("report-type");

        console.log("Selected report type:", reportType);

        switch(reportType) {
            case "all":
                generatePdf("/all/passengers");
                break;
            case "local":
                generatePdf("/local/passengers");
                break;
            case "foreign":
                generatePdf("/foreign/passengers");
                break;
            case "active-local":
                generatePdf("/local/active/passengers");
                break;
            case "blocked-local":
                generatePdf("/local/blocked/passengers");
                break;
            case "active-foreign":
                generatePdf("/foreign/active/passengers");
                break;
            case "blocked-foreign":
                generatePdf("/foreign/blocked/passengers");
                break;
            case "active":
                generatePdf("/all/active/passengers");
                break;
            case "blocked":
                generatePdf("/all/blocked/passengers");
                break;
        }
    });

    function generatePdf(url = null) {
        let startUrl = "http://localhost:8080/api/v1/raillankapro/pdf/download"
        if (url) {
            toastr.info("Generating PDF report...");
            setTimeout(() => {
            const myHeaders = new Headers();
            myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1Njk4MzM4MSwiZXhwIjoxMDc1Njk4MzM4MX0.IZXH8px-C5D1hzk87isH5X-CTzJnp9vJ3SX4BCVpoPI");

            const requestOptions = {
                method: "GET",
                headers: myHeaders,
                redirect: "follow"
            };

            fetch(`${startUrl}${url}`, requestOptions)
            .then(async (response) => {
                let formattedUrl =  url.substring(1); 
                let fileName = url.replace(/\//g, "_").replace(/^_/, ""); 
                
                const blob = await response.blob();
                return ({ blob, fileName });
            })
            .then(({ blob, fileName }) => {
                const url = window.URL.createObjectURL(blob);
                const $a = $('<a />', {
                    href: url,
                    download: fileName + "_" + Date.now() + ".pdf" // semicolon nathuwa
                }).appendTo("body");
                $a[0].click();
                $a.remove();
                window.URL.revokeObjectURL(url);

                 toastr.success("PDF report downloaded successfully");
            })
            .catch((error) => {
                    console.error(error);
                    toastr.error("Failed to download PDF. Please try again.");
            
            });
        }, 2000);

        }else{
            toastr.error("URL Null!!!")
        }
    }


    $("#closeExportModal , #closeExportBtn").on("click" , function() {
        closeModal("exportPdfModal")
    })

    $("#filterPassenger").on("input" , function() {
        $("#statusFilter").val("");
        $("#typeFilter").val("");
        const keyword = encodeURIComponent($(this).val().trim());
        currentKeyword = keyword; 
        currentPage = 1;
        fetchPassengers(currentPage, currentKeyword);
    });

    $("#statusFilter").on("change" , function() {
        passengerType = null
        const value = $(this).val();
        $("#typeFilter").val("");
        $("#filterPassenger").val("");
        status = value;
        if (status === "") {
            fetchPassengers(currentPage)
            return
        }

        fetchPassengers(currentPage , currentKeyword , status)

    })

    $("#typeFilter").on("change" , function() {
        status = null
        $("#statusFilter").val("");
        $("#filterPassenger").val("");
        const value = $(this).val();
        passengerType = value;

         if (passengerType === "") {
            fetchPassengers(currentPage)
            return
        }

        fetchPassengers(currentPage , currentKeyword , null , passengerType)
    });

    

    $(document).on("click", ".block-btn, .unblock-btn", function () {
        selectedBtn = $(this); 
        const passengerId = selectedBtn.data("id");
        const currentStatus = selectedBtn.data("status");
        const action = currentStatus ? "block" : "unblock";

        $("#blockPassengerId").val(passengerId);
        $("#blockAction").val(action);

        if (action === "block") {
            $("#blockModalTitle").text("Block Passenger");
            $("#blockModalSubtitle").text("Restrict passenger account access");
            $("#blockModalMessage").text(
                "Are you sure you want to block this passenger? They will not be able to access their account until unblocked."
            );
            $("#confirmBlockBtn").html("Block Passenger");
        } else {
            $("#blockModalTitle").text("Unblock Passenger");
            $("#blockModalSubtitle").text("Restore passenger account access");
            $("#blockModalMessage").text(
                "Are you sure you want to unblock this passenger? They will regain access to their account."
            );
            $("#confirmBlockBtn").html("Unblock Passenger");
        }

        openModal("blockModal");
    });

    $(document).on("click", "#viewPassengerBtn", function() {
        const id = $(this).data("id");
        openModal("passengerModal")

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NzA5NjM2MSwiZXhwIjoxMDc1NzA5NjM2MX0.J81-5suzaOXcVRHjN8XOXOaTcaTl8MVHrq9n5eaFzJw");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/passenger/getpassenger?id=${id}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code != 200) {
                toastr.error(result.message)
                return;
            }

            if (result.code === 200) {
                const data = result. data;

                $("#viewPassengerName").text(`${data.title} ${data.firstName} ${data.lastName}`)
                
                if(data.passengerType === "LOCAL") {
                    $("#viewPassengerType")
                    .removeClass("bg-yellow-100 text-yellow-800")
                    .addClass("bg-blue-100 text-blue-800")
                    .text("LOCAL")
                }else {
                     $("#viewPassengerType")
                    .addClass("bg-yellow-100 text-yellow-800")
                    .removeClass("bg-blue-100 text-blue-800")
                    .text("FOREGIN")
                }

                if(data.blocked) {
                    $("#viewStatus")
                    .addClass("status-inactive")
                    .removeClass("status-active")
                    .text("Blocked")

                    $("#viewStatusIndicator")
                    .addClass("bg-red-500")
                    .removeClass("bg-green-500")

                    $("#viewStatusBadge")
                    .removeClass("bg-green-100 text-green-800")
                    .addClass("bg-red-100 text-red-800")
                    .text("Blocked Accont")
                }else {
                    $("#viewStatus")
                    .removeClass("status-inactive")
                    .addClass("status-active")
                    .text("Active")

                    $("#viewStatusIndicator")
                    .removeClass("bg-red-500")
                    .addClass("bg-green-500")
                    
                    $("#viewStatusBadge")
                    .addClass("bg-green-100 text-green-800")
                    .removeClass("bg-red-100 text-red-800")
                    .text("Active Accont")

                }

                $("#viewPassengerId").text(data.passengerId)
                $("#viewTitle").text(data.title)
                $("#viewUsername").text(data.username)
                $("#viewPassengerNameDetail").text(`${data.firstName} ${data.lastName}`)
                $("#viewPhone").text(data.phoneNumber)
                $("#viewEmail").text(data.email)
                $("#viewIdType").text(data.idType)
                $("#viewIdNumber").text(data.idNumber)
                $("#viewEmail").text(data.email)




            }
        })
        .catch((error) => console.error(error));


    });

   
    $(document).on("click", "#confirmBlockBtn", function () {
        if (!selectedBtn) return;

        const passengerId = selectedBtn.data("id");
        let blocked = selectedBtn.data("status") === true || selectedBtn.data("status") === "true";

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NzA5NjM2MSwiZXhwIjoxMDc1NzA5NjM2MX0.J81-5suzaOXcVRHjN8XOXOaTcaTl8MVHrq9n5eaFzJw");

        const requestOptions = {
            method: "PUT",
            headers: myHeaders,
            redirect: "follow"
        };

        fetch(`http://localhost:8080/api/v1/raillankapro/passenger/changestatus/${passengerId}/${blocked}`, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)

            if (result.code != 200) {
                toastr.error(result.mesasge)
                return;
            }

            if (result.code === 200) {
                if(result.data) {
                    toastr.warning(result.message)
                }else {
                    toastr.success(result.message)
                }

                fetchPassengers(currentPage , currentKeyword , status)
            }
        })
        .catch((error) => console.error(error));

        closeModal("blockModal");
        selectedBtn = null;

    });


    function fetchPassengers(page, keyword = null, status = null, type = null){
        let url = "";
        if (status) {
            url = `http://localhost:8080/api/v1/raillankapro/passenger/filter/${page}/7?status=${status}`;
        }else if (type){
            url = `http://localhost:8080/api/v1/raillankapro/passenger/filter/${page}/7?type=${type}`;
        }else if (keyword && keyword.length >= 2) {
            url = `http://localhost:8080/api/v1/raillankapro/passenger/filter/${page}/7?keyword=${keyword}`;
        } else {
            url = `http://localhost:8080/api/v1/raillankapro/passenger/getall/${page}/7`;
        }

        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW51MjAwNiIsImlhdCI6MTc1NzA5NjM2MSwiZXhwIjoxMDc1NzA5NjM2MX0.J81-5suzaOXcVRHjN8XOXOaTcaTl8MVHrq9n5eaFzJw");

        const requestOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow"
        };

        fetch(url, requestOptions)
        .then((response) => response.json())
        .then((result) => {
            console.log(result)
            if (result.code != 200) {
                toastr.error(result.mesage)
                return;
            }

            if (result.code === 200) {
                
                const passengers = result.data;
                totalPages = result.totalPages;

                $('#currentPage').text(result.startNumber);
                $('#totalPage').text(result.totalItems);
                $("#selectedLastRowData").text(result.endNumber);

                $("#passengerTable tbody").empty();

                if (passengers.length === 0) {
                    $("#passengerTable tbody").append(
                        `<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">
                            No Passengers found
                        </td></tr>`
                    );

                }
                

                passengers.forEach(passenger => {
                    const randomColor = getRandomColor();
                    let statusText = passenger.blocked ? "bloked" : "active";
                    let statusClass = passenger.blocked ? "status-inactive" : "status-active";
                    let color;
                    let passengerType = passenger.passengerType;

                    if (passengerType === "LOCAL") {
                        color = "blue"
                    }else {
                        color = "orange"
                    }

                    let actionButton;

                    if (passenger.blocked) {
                        actionButton = `
                            <button
                                class="text-green-600 hover:text-green-900 unblock-btn toggle-block-btn"
                                data-id="${passenger.passengerId}"
                                data-status="false"
                                
                            >
                                 <i class="fas fa-lock-open "></i> 
                            </button>
                        `;
                    } else {
                    
                        actionButton = `
                            <button
                                class="text-red-600 hover:text-red-900 block-btn toggle-block-btn"
                                data-id="${passenger.passengerId}"
                                data-status="true"
                               
                            >
                                <i class="fas fa-lock "></i> 
                            </button>
                        `;
                    }

                    $("#passengerTable tbody").append(

                         `<tr>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="flex items-center">
                                <div class="flex-shrink-0 h-10 w-10">
                                    <div
                                    class="h-10 w-10 rounded-full bg-${randomColor}-100 flex items-center justify-center"
                                    >
                                    <i class="fas fa-user text-${randomColor}-600"></i>
                                    </div>
                                </div>
                                <div class="ml-4">
                                    <div class="text-sm font-medium text-gray-900">
                                    ${passenger.title} ${passenger.firstName} ${passenger.lastName}
                                    </div>
                                    <div class="text-sm text-gray-500">${passenger.passengerId}-${passenger.username}</div>
                                </div>
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm text-gray-900">
                                    <a href="tel:${passenger.phoneNumber}" class="text-gray-900">
                                        ${passenger.phoneNumber}
                                    </a>
                                </div>
                                <div class="text-sm text-gray-500">
                                    <a href="mailto:${passenger.email}" class="text-blue-600">
                                        ${passenger.email}
                                    </a>
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="text-sm text-gray-900">${passenger.idType}</div>
                                <div class="text-sm text-gray-500">${passenger.idNumber}</div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <span class="passenger-type-badge bg-${color}-100 text-${color}-800"
                                >${passengerType}</span
                                >
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <span
                                class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}"
                                >${statusText}</span
                                >
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                <button
                                id= "viewPassengerBtn"
                                class="text-blue-600 hover:text-blue-900 view-btn mr-3"
                                data-id="${passenger.passengerId}"
                                
                                >
                                <i class="fas fa-eye"></i>
                                </button>
                                ${actionButton}
                            </td>
                        </tr>
                        `

                     );
                });
            }
                updatePaginationControls(page, totalPages);
            

        })
        .catch((error) => console.error(error));

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
            fetchPassengers(currentPage, currentKeyword , status, passengerType);
            }
        });
    }

    $("#btnFirst").on("click", () => {
        if (currentPage > 1) {
            currentPage = 1;
            fetchPassengers(currentPage, currentKeyword, status, passengerType);
        }
    });

    $("#btnBack").on("click", () => {
        if (currentPage > 1) {
            currentPage -= 1;
            fetchPassengers(currentPage, currentKeyword, status, passengerType);
        }
    });

    $("#btnNext").on("click", () => {
    if (currentPage < totalPages) {
        currentPage += 1;
        fetchPassengers(currentPage, currentKeyword, status, passengerType);
    }
    });

    $("#btnLast").on("click", () => {
    if (currentPage < totalPages) {
        currentPage = totalPages;
        fetchPassengers(currentPage, currentKeyword , status, passengerType);
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

});