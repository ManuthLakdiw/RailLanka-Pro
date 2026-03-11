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
    console.log(result);
    if (result && result.code === 200) {
      const booking = result.data;
      const phoneNumber = booking.payeeInfo.phoneNumber;

      // ✅ UI Update
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

      // ✅ Now Call SMS API (phoneNumber available!)
      const requestOptions2 = {
        method: "POST",
        redirect: "follow"
      };

      console.log("Booking ID for SMS:", booking.bookingId);
      console.log("Phone Number for SMS:", phoneNumber);

      fetch(`http://localhost:8080/api/v1/raillankapro/auth/sms/${booking.bookingId}/${phoneNumber}`, requestOptions2)
        .then((response) => response.json())
        .then((result) => console.log("SMS API Result:", result))
        .catch((error) => console.error("SMS API Error:", error));
    }
  })
  .catch((error) => console.error("Booking Fetch Error:", error));

    

  



$(".download-btn").on("click", async function () {
    const pdfUrl = `http://localhost:8080/api/v1/raillankapro/auth/download/ticket?bookingid=${bookingId}`;
    const qrUrl = `http://localhost:8080/api/v1/raillankapro/auth/qr/${bookingId}`;

    try {
        // 1️⃣ PDF DOWNLOAD
        const pdfResponse = await fetch(pdfUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/pdf',
            }
        });

        if (!pdfResponse.ok) throw new Error(`PDF HTTP error! status: ${pdfResponse.status}`);

        const pdfBlob = await pdfResponse.blob();
        const pdfBlobUrl = window.URL.createObjectURL(pdfBlob);

        const pdfLink = document.createElement('a');
        pdfLink.href = pdfBlobUrl;
        pdfLink.download = `ticket_${bookingId}.pdf`;
        pdfLink.click();
        window.URL.revokeObjectURL(pdfBlobUrl);

        const qrResponse = await fetch(qrUrl, { method: 'GET' });

        if (!qrResponse.ok) throw new Error(`QR HTTP error! status: ${qrResponse.status}`);

        const qrBlob = await qrResponse.blob();
        const qrBlobUrl = window.URL.createObjectURL(qrBlob);

        const qrLink = document.createElement('a');
        qrLink.href = qrBlobUrl;
        qrLink.download = `qr_${bookingId}.png`;
        qrLink.click();
        window.URL.revokeObjectURL(qrBlobUrl);

    } catch (error) {
        console.error('Download failed:', error);
        alert('Failed to download ticket or QR. Please try again.');
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