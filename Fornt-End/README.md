# 🎨 RailLanka Pro — Frontend

The frontend is a **pure Vanilla HTML5 / CSS3 / JavaScript (ES6+)** multi-page application split into two completely separate sub-projects: the **Staff/Admin Dashboard** (`system-user/`) and the **Passenger Booking Portal** (`passenger/`). There is no bundler or framework — every page is a standalone HTML file that talks directly to the backend REST API.

> 🔗 **Root Project:** [Back to Main README](../README.md) &nbsp;|&nbsp; ⚙️ **Backend:** [Backend README](../Back-End/README.md)

---

## 📑 Table of Contents

1. [Tech Stack](#-tech-stack)
2. [Prerequisites](#-prerequisites)
3. [Installation & Setup](#-installation--setup)
4. [Environment Variables](#-environment-variables-api-base-url)
5. [Folder Structure](#-folder-structure)
6. [Sub-Project Breakdown](#-sub-project-breakdown)
7. [Core Concepts](#-core-concepts)
8. [Page Reference](#-page-reference)
9. [Available Scripts](#-available-scripts)

---

## 🛠️ Tech Stack

| Category | Technology | Notes |
|----------|-----------|-------|
| **Markup** | HTML5 | Semantic elements throughout |
| **Styling** | Tailwind CSS, Vanilla CSS3 | Flexbox, Grid, custom properties, animations |
| **Scripting** | Vanilla JavaScript (ES6+) | `async/await`, Fetch API, modules |
| **HTTP Client** | Browser `fetch()` API | All REST API calls |
| **Icons** | Font Awesome (CDN) | Icon library used across both sub-projects |
| **Fonts** | Google Fonts (CDN) | Premium typography |
| **PDF Viewer** | Browser native / download | Ticket & report PDFs served from backend |
| **Normalize** | `normalize.css` | CSS reset for cross-browser consistency |
| **Server** | Any static file server | No build step required |

---

## ✅ Prerequisites

Since this is a pure static frontend, the requirements are minimal:

- 🌐 **A modern web browser** — Chrome 90+, Firefox 88+, Edge 90+, or Safari 14+
- ⚙️ **Backend API running** — The Spring Boot backend must be running at `http://localhost:8080` (see [Backend README](../Back-End/README.md))
- 🖥️ **A static file server** (optional for local dev) — Live Server (VS Code), `http-server`, or `npx serve`

> 💡 You can open HTML files directly in your browser (`File → Open`), but using a local server is strongly recommended to avoid CORS and `fetch` restrictions.

### Optional: Node.js for a local dev server
```bash
# Node.js is only needed if you want a live-reload dev server
node --version   # v18+ recommended
```

---

## 🚀 Installation & Setup

### Step 1 — Clone the Repository
```bash
git clone https://github.com/ManuthLakdiw/RailLanka-Pro.git
cd "RailLanka-Pro/Fornt-End"
```

### Step 2 — Start the Backend
Ensure the Spring Boot backend is running at `http://localhost:8080` before opening any frontend page. See [Backend README](../Back-End/README.md).

### Step 3 — Serve the Frontend

**Option A: VS Code Live Server (Recommended)**
1. Open the `Fornt-End/` folder in VS Code.
2. Right-click `system-user/index.html` → **"Open with Live Server"**.
3. Repeat for `passenger/index.html`.

**Option B: `npx serve`**
```bash
# Serve the Staff Dashboard
npx serve system-user

# Serve the Passenger Portal
npx serve passenger
```

**Option C: `http-server`**
```bash
npm install -g http-server

# Staff Dashboard
http-server system-user -p 3000

# Passenger Portal
http-server passenger -p 3001
```

**Option D: Open directly in browser**
```
# Staff Dashboard
open Fornt-End/system-user/index.html

# Passenger Portal
open Fornt-End/passenger/index.html
```

---

## 🔧 Environment Variables (API Base URL)

There is no `.env` file since this is a static project. The **API base URL** is defined as a constant at the top of each JavaScript file. To point to a different backend, search and replace this value:

```javascript
// Example from system-user/js/manage-train.js
const BASE_URL = "http://localhost:8080/api/v1/raillankapro";
```

If you deploy the backend to a remote server, update this constant in every JS file to match the deployed URL:

```javascript
const BASE_URL = "https://your-backend-domain.com/api/v1/raillankapro";
```

> 💡 **Pro Tip:** To make this easier to maintain, create a single `config.js` file exporting `BASE_URL` and import it into each page's JS file.

---

## 📁 Folder Structure

```
Fornt-End/
│
├── animation.html              ← Shared loading/animation screen
├── logging-expired.html        ← Session expiry page (shown on 401)
│
├── system-user/                ← 👨‍💼 STAFF / ADMIN DASHBOARD
│   ├── index.html              ← Main dashboard entry point (login redirect)
│   │
│   ├── pages/                  ← All dashboard HTML pages
│   │   ├── signin.html
│   │   ├── forgot-password.html
│   │   ├── admin-dashboard.html
│   │   ├── manage-train.html
│   │   ├── manage-station.html
│   │   ├── manage-schedule.html
│   │   ├── manage-employee.html
│   │   ├── manage-passenger.html
│   │   ├── manage-counter.html
│   │   ├── manage-station-master.html
│   │   ├── manage-salary.html
│   │   ├── contact-it-support.html
│   │   ├── report.html
│   │   ├── profile.html
│   │   ├── stationmaster-dashboard.html
│   │   └── anim.html          ← Page transition animation
│   │
│   ├── js/                     ← JavaScript logic (one file per page)
│   │   ├── index.js            ← Auth guard + role-based redirect
│   │   ├── signin.js           ← Login flow + token storage
│   │   ├── forgot-password.js  ← OTP-based password reset
│   │   ├── admin-dashboard.js  ← Charts, KPI counters, analytics
│   │   ├── manage-train.js     ← Full CRUD + pagination + filtering
│   │   ├── manage-station.js   ← Station CRUD
│   │   ├── manage-schedule.js  ← Schedule CRUD + conflict detection
│   │   ├── manage-employee.js  ← Employee CRUD
│   │   ├── manage-passenger.js ← Passenger management + block/unblock
│   │   ├── manage-counter.js   ← Counter CRUD
│   │   ├── manage-station-master.js
│   │   ├── report.js           ← PDF report generation triggers
│   │   ├── contact-it-support.js
│   │   ├── anim.js             ← Animation controller
│   │   └── icon-colors.js      ← Dynamic sidebar icon styling
│   │
│   ├── styles/                 ← CSS per page + shared utilities
│   │   ├── normalize.css
│   │   ├── manage-train.css
│   │   ├── manage-employee.css
│   │   ├── manage-schedule.css
│   │   ├── manage-station.css
│   │   ├── manage-pasenger.css
│   │   ├── report.css
│   │   ├── admin-dashbaord.css
│   │   ├── anim.css
│   │   ├── forgot-password.css
│   │   ├── it-support.css
│   │   └── password-strength.css
│   │
│   └── assets/                 ← Images, icons, logos
│
└── passenger/                  ← 🧳 PASSENGER BOOKING PORTAL
    ├── index.html              ← Main passenger portal (all-in-one SPA-style)
    │
    ├── pages/                  ← Additional passenger pages
    │   ├── signin.html
    │   ├── signup.html
    │   ├── reset-password.html
    │   ├── booking-success.html
    │   ├── logging-expired.html
    │   └── anim.html
    │
    ├── js/                     ← JavaScript for passenger portal
    │   ├── index.js            ← Core: search, results, seat selection, booking
    │   ├── signin.js           ← Passenger login
    │   ├── signup.js           ← Registration + validation
    │   ├── reset-password.js   ← OTP password reset
    │   └── booking-success.js  ← Post-booking confirmation + PDF/SMS trigger
    │
    ├── styles/                 ← CSS for passenger portal
    │   ├── normalize.css
    │   ├── index.css           ← Main portal styles (dark/light theme)
    │   ├── signin.css
    │   ├── signup.css
    │   ├── reset-password.css
    │   └── booking-success.css
    │
    └── assets/
        └── images/             ← Banner images, logos
```

---

## 🏗️ Sub-Project Breakdown

### 👨‍💼 `system-user/` — Staff & Admin Dashboard

This is the **internal control panel** used by admins, station masters, and counter operators. Access is role-gated — the `index.js` auth guard reads the JWT from `localStorage`, decodes the role, and redirects to the correct dashboard page.

**User Roles & Landing Pages:**

| Role | Landing Page |
|------|-------------|
| `ADMIN` | `pages/admin-dashboard.html` |
| `STATION_MASTER` | `pages/stationmaster-dashboard.html` |
| `COUNTER` | `pages/manage-passenger.html` |

**Admin Dashboard Features (`admin-dashboard.js`):**
- Renders real-time KPI cards (total trains, active schedules, passenger counts)
- Fetches analytics from multiple API endpoints simultaneously
- Draws bar charts and pie charts for train types, schedule frequencies

**Management Pages (e.g., `manage-train.js`):**
- Full **CRUD** operations via `fetch()` calls to the backend API
- **Server-side pagination** — page controls send `{pageNo}/{pageSize}` path params
- **Multi-mode filtering** — keyword search, category filter, status filter all hit separate API query params
- **Inline editing** — form pre-fills from API response on "Edit" button click
- **Optimistic UI** — table refreshes after every create/update/delete

---

### 🧳 `passenger/` — Passenger Booking Portal

This is the **public-facing website** where passengers search for trains, select seats, and complete bookings.

**`index.html` + `index.js` — Core Booking Flow:**

The main page (`index.js` — 85 KB) is a large, feature-rich single-file controller managing:

1. **Train Search** — user picks departure station, destination, date → `POST /auth/schedules/{page}/{size}`
2. **Results Display** — paginated schedule cards with train details, departure/arrival times, class availability
3. **Price Calculation** — `POST /auth/calc/clases/ticket/price?scheduleid=` returns per-class pricing
4. **Seat Availability** — `GET /auth/get/booked/seats?traveldate=&schedule=` fetches taken seats
5. **Manual Seat Selection** — interactive seat map for first-class bookings (rows A–F, numbered seats)
6. **Booking Placement** — `POST /auth/check` locks the seat and creates a pending booking
7. **Payment** — redirects to PayHere payment gateway with pre-filled merchant details
8. **Post-Booking** — `booking-success.js` triggers PDF ticket download and optional SMS confirmation

---

## 🧠 Core Concepts

### 🔐 Authentication & Token Management

Both sub-projects manage JWTs manually without any library:

```javascript
// Store tokens after login
localStorage.setItem("accessToken", data.accessToken);
localStorage.setItem("refreshToken", data.refreshToken);
localStorage.setItem("role", data.role);

// Attach to every protected request
headers: {
  "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
  "Content-Type": "application/json"
}
```

**Auto Token Refresh:** When the backend returns `401 Unauthorized` (expired access token), the JS intercepts the response and calls `POST /auth/refreshtoken` with the stored refresh token to silently obtain a new access token and retry the request.

**Session Expiry:** If the refresh token is also expired or invalid, the user is redirected to `logging-expired.html` which shows a friendly session-expired screen before redirecting to login.

---

### 📋 Server-Side Pagination

All list pages use **server-side pagination** — the browser never loads all records at once:

```javascript
// Example paginated fetch
async function loadTrains(pageNo = 1, pageSize = 10) {
  const res = await fetch(`${BASE_URL}/train/getall/${pageNo}/${pageSize}`, {
    headers: { "Authorization": `Bearer ${token}` }
  });
  const data = await res.json();
  // data.totalPages, data.currentPage, data.totalItems
  renderTable(data.data);
  renderPaginationControls(data);
}
```

The `PaginatedResponse` from the backend includes `currentPage`, `totalPages`, `totalItems`, `startNumber`, and `endNumber` — all rendered into pagination UI controls.

---

### 🎨 Theming (Passenger Portal)

The passenger portal supports **Light & Dark themes** toggled via a UI switch:

```javascript
// Toggle class on <body>
document.body.classList.toggle("dark-theme");
localStorage.setItem("theme", isDark ? "dark" : "light");
```

CSS custom properties (variables) switch the entire colour scheme:

```css
:root {
  --bg-color: #ffffff;
  --text-color: #1a1a2e;
  --accent-color: #0056b3;
}

body.dark-theme {
  --bg-color: #0d1117;
  --text-color: #e6edf3;
  --accent-color: #58a6ff;
}
```

---

### 🪑 First-Class Seat Selection

An interactive seat map is rendered in `index.js` (passenger portal):

- Grid layout matching physical train carriage (rows: A–F, seats: 1–N)
- Taken seats (from API) are rendered as **disabled/red**
- Available seats are **green and clickable**
- Selected seats are highlighted **blue**
- On confirmation, selected seat coordinates are sent with the booking payload

---

### 📊 Password Strength Indicator

Both sign-up flows (`signup.js`, `manage-counter.js`, etc.) include a real-time password strength meter:

```javascript
function evaluatePasswordStrength(password) {
  // Checks: length >= 8, uppercase, lowercase, digits, special chars
  // Returns: "Weak" | "Fair" | "Strong" | "Very Strong"
}
```

Visually rendered via `password-strength.css` as a segmented colour bar.

---

### 📄 PDF Download

PDF tickets and reports are downloaded directly from the backend as `application/pdf` blobs:

```javascript
const response = await fetch(`${BASE_URL}/auth/download/ticket?bookingid=${bookingId}`, {
  headers: { "Authorization": `Bearer ${token}` }
});
const blob = await response.blob();
const url = URL.createObjectURL(blob);
const a = document.createElement("a");
a.href = url;
a.download = "ticket.pdf";
a.click();
```

---

## 📑 Page Reference

### Staff Dashboard (`system-user/pages/`)

| Page | JS File | Description |
|------|---------|-------------|
| `signin.html` | `signin.js` | Staff login with role-based redirect |
| `forgot-password.html` | `forgot-password.js` | OTP email → verify → reset password |
| `admin-dashboard.html` | `admin-dashboard.js` | KPIs, charts, analytics overview |
| `manage-train.html` | `manage-train.js` | Train CRUD, filter, activate/deactivate |
| `manage-station.html` | `manage-station.js` | Station CRUD |
| `manage-schedule.html` | `manage-schedule.js` | Schedule CRUD, frequency, conflict detection |
| `manage-employee.html` | `manage-employee.js` | Employee records, by-station filter |
| `manage-passenger.html` | `manage-passenger.js` | View, block/unblock passengers |
| `manage-counter.html` | `manage-counter.js` | Counter operator CRUD |
| `manage-station-master.html` | `manage-station-master.js` | Station master CRUD |
| `report.html` | `report.js` | Trigger & download all PDF reports |
| `contact-it-support.html` | `contact-it-support.js` | Submit IT support tickets |
| `profile.html` | *(inline)* | User profile & password change |
| `stationmaster-dashboard.html` | *(inline)* | Station master's limited dashboard |

### Passenger Portal (`passenger/pages/`)

| Page | JS File | Description |
|------|---------|-------------|
| `index.html` | `index.js` | Search → results → seat → payment |
| `signin.html` | `signin.js` | Passenger login |
| `signup.html` | `signup.js` | New passenger registration |
| `reset-password.html` | `reset-password.js` | OTP-based password reset |
| `booking-success.html` | `booking-success.js` | Booking confirmation + PDF/SMS |

---

## 📜 Available Scripts

Since there is no `package.json` at the root, these are convenience commands using global tools:

```bash
# ── Serve Staff Dashboard (port 3000) ──────────────────────────
npx serve Fornt-End/system-user -l 3000

# ── Serve Passenger Portal (port 3001) ─────────────────────────
npx serve Fornt-End/passenger -l 3001

# ── Live reload with browser-sync ──────────────────────────────
npx browser-sync start --server "Fornt-End/system-user" --files "**/*.html, **/*.js, **/*.css"

# ── Quick HTTP server (Python fallback) ────────────────────────
# Staff Dashboard
python3 -m http.server 3000 --directory Fornt-End/system-user

# Passenger Portal
python3 -m http.server 3001 --directory Fornt-End/passenger
```

---

## 🌐 Browser Support

| Browser | Version | Support |
|---------|---------|---------|
| Chrome | 90+ | ✅ Full |
| Firefox | 88+ | ✅ Full |
| Edge | 90+ | ✅ Full |
| Safari | 14+ | ✅ Full |
| Internet Explorer | Any | ❌ Not supported |

---

> ⚙️ **Backend Documentation:** [Back-End README](../Back-End/README.md)  
> 🏠 **Root Project:** [Main README](../README.md)
