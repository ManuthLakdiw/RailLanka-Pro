$(document).ready(function () {
  createConfetti();

  const bookingData = JSON.parse(sessionStorage.getItem("recentBookingData"));
    console.log("Booking Object:", bookingData);

    const urlParams = new URLSearchParams(window.location.search);
    const bookingId = urlParams.get("bookingId");
    console.log("Booking ID:", bookingId);

    const requestOptions = {
        method: "GET",
        redirect: "follow"
    };

    fetch(`http://localhost:8080/api/v1/raillankapro/auth/booking/detail/by?bookingid=${bookingId}`, requestOptions)
    .then((response) => response.json())
    .then((result) => {
        console.log(result)
        if (result && result.code === 200) {
            const booking = result.data;
            $("#booking-reference").text(booking.bookingId);
            $("#route-detail").text(booking.departureStation + " to " + booking.destinationStation);
            $("#train-detail").text(booking.trainName);
            $("#class-detail").text(booking.travelClass);
            $("#departure-detail").text(`${booking.formattedTravelDate}, ${booking.departureTime}`);
            $("#arrival-detail").text(booking.arrivalTime);
            $("#passenger-name").text(booking.payeeInfo.firstName);
            $("#passenger-id").text(booking.payeeInfo.nicOrPassport);
            $("#seat-detail").text(booking.formatedselectedSeat);
            $("#adults-count").text(booking.adultCount);
            $("#children-count").text(booking.childCount);
            $("#amount-paid").text(booking.formattedTotalAmount);
        }
    })
    .catch((error) => console.error(error));
    

  



$(".download-btn").on("click", async function () {
    const url = `http://localhost:8080/api/v1/raillankapro/auth/download/ticket?bookingid=${bookingId}`;
    
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/pdf',
                // Add authorization header if needed
                // 'Authorization': 'Bearer ' + token
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // Convert response to blob
        const blob = await response.blob();
        
        // Create URL for blob
        const blobUrl = window.URL.createObjectURL(blob);
        
        // Create link and trigger download
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = `ticket_${bookingId}.pdf`;
        link.click();
        
        // Clean up
        window.URL.revokeObjectURL(blobUrl);
        
    } catch (error) {
        console.error('Download failed:', error);
        alert('Failed to download ticket. Please try again.');
    }
});





  function createConfetti() {
    const $confettiContainer = $("#confetti-container");
    const colors = ["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6"];

    for (let i = 0; i < 50; i++) {
        const $confetti = $("<div></div>")
        .addClass("confetti")
        .css({
            left: Math.random() * 100 + "vw",
            animation: `confettiFall ${Math.random() * 3 + 2}s linear forwards`,
            background: colors[Math.floor(Math.random() * colors.length)],
            width: Math.random() * 10 + 5 + "px",
            height: Math.random() * 10 + 5 + "px"
        });

        $confettiContainer.append($confetti);
    }
    }

});