$(document).ready(function() {
    const authContainer = $('#authContainer');

    // Check both localStorage and sessionStorage
    const accessToken = localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');
    const userName = localStorage.getItem('userName') || sessionStorage.getItem('userName') || 'User';

    if (accessToken) {
        // User is logged in - show profile dropdown with logout
        authContainer.html(`
            <div class="relative group">
                <button class="flex items-center space-x-2 text-gray-700">
                    <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                        <i class="fas fa-user text-blue-700"></i>
                    </div>
                    <span>${userName}</span>
                    <i class="fas fa-chevron-down text-xs transition-transform group-hover:rotate-180"></i>
                </button>

                <!-- Dropdown Menu -->
                <div class="absolute right-0 top-full mt-2 w-48 bg-white rounded-md shadow-lg py-1 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 z-50">
                    <div class="px-4 py-2 border-b border-gray-100 text-xs text-gray-500">
                        Signed in as <span>${userName}</span>
                    </div>
                    <a href="#" class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-700">
                        <i class="fas fa-user-circle mr-2"></i>Profile
                    </a>
                    <a href="#" class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-red-50 hover:text-red-700" id="logoutButton">
                        <i class="fas fa-sign-out-alt mr-2"></i>Logout
                    </a>
                </div>
            </div>
        `);

        // Add logout functionality
        $('#logoutButton').on('click', function(e) {
            e.preventDefault();
            // Clear token and username from both storages
            localStorage.removeItem('accessToken');
            localStorage.removeItem('userName');
            localStorage.removeItem('refreshToken');
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('userName');
            sessionStorage.removeItem('refreshToken');

            // Refresh to update UI
            location.reload();
        });

    } else {
        // User is not logged in - show sign in button
        authContainer.html(`
            <a
                href="pages/anim.html"
                class="flex items-center gap-2 bg-gradient-to-r from-blue-600 to-blue-800 hover:from-blue-700 hover:to-blue-900 text-white font-medium rounded-lg text-sm px-5 py-2.5 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg focus:ring-4 focus:outline-none focus:ring-blue-300 shadow-md"
            >
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-5 w-5"
                    viewBox="0 0 20 20"
                    fill="currentColor"
                >
                    <path
                        fill-rule="evenodd"
                        d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                        clip-rule="evenodd"
                    />
                </svg>
                Sign in
            </a>
        `);
    }


    $("#accessAdminDashBoardBtn").on("click", function() {
         window.location.href = '/system-user/pages/admin-dashboard.html';
    });
});