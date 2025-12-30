document.addEventListener('DOMContentLoaded', () => {
    const themeBtn = document.getElementById('theme-toggle-btn');
    const langBtn = document.getElementById('language-toggle-btn');
    const mobileLangBtn = document.getElementById('mobile-language-btn');
    const menuToggle = document.getElementById('menu-toggle');
    const menuIcon = document.getElementById('menu-icon');
    const mobileMenu = document.getElementById('mobile-menu');
    const languageDropdown = document.querySelector('.language-dropdown');
    const mobileLanguageDropdown = document.getElementById('mobile-language-dropdown');
    const languageContainer = document.querySelector('.language');
    const navbar = document.getElementById('navigation-bar');

    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    const savedLang = localStorage.getItem('language') || 'pt-BR';
    const pathPrefix = window.location.pathname.split('/').filter(Boolean)[0];
    const desiredPrefix = savedLang === 'pt-BR' ? 'br' : 'us';

    if (pathPrefix !== desiredPrefix) {
        const currentPath = window.location.pathname.replace(/^\/(br|us)/, '');
        const newPath = `/${desiredPrefix}${currentPath}${window.location.search}${window.location.hash}`;
        window.history.replaceState(null, '', newPath);
    }

    function handleScroll() {
        if (!navbar) return;
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    }

    if (navbar) {
        window.addEventListener('scroll', handleScroll);
        handleScroll();
    }

    function toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    }

    function toggleMobileMenu() {
        const isActive = mobileMenu.classList.toggle('active');
        if (menuIcon) {
            menuIcon.className = isActive ? 'fa-solid fa-times' : 'fa-solid fa-bars';
        }
        document.body.style.overflow = isActive ? 'hidden' : '';
    }

    if (themeBtn) {
        themeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            toggleTheme();
        });
    }

    if (langBtn) {
        langBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            languageDropdown.classList.toggle('open');
            langBtn.setAttribute('aria-expanded', languageDropdown.classList.contains('open'));
        });
    }

    if (mobileLangBtn) {
        mobileLangBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            mobileLanguageDropdown.classList.toggle('open');
            const chevron = mobileLangBtn.querySelector('.mobile-chevron');
            if (chevron) {
                chevron.classList.toggle('rotate');
            }
        });
    }

    if (menuToggle) {
        menuToggle.addEventListener('click', (e) => {
            e.preventDefault();
            toggleMobileMenu();
        });
    }

    document.querySelectorAll('.mobile-link').forEach(link => {
        link.addEventListener('click', () => {
            mobileMenu.classList.remove('active');
            if (menuIcon) {
                menuIcon.className = 'fa-solid fa-bars';
            }
            document.body.style.overflow = '';
        });
    });

    document.addEventListener('click', (e) => {
        if (languageContainer && !languageContainer.contains(e.target)) {
            if (languageDropdown) {
                languageDropdown.classList.remove('open');
                if (langBtn) langBtn.setAttribute('aria-expanded', 'false');
            }
        }
        if (mobileMenu.classList.contains('active') && !mobileMenu.contains(e.target) && !menuToggle.contains(e.target)) {
            mobileMenu.classList.remove('active');
            if (menuIcon) {
                menuIcon.className = 'fa-solid fa-bars';
            }
            document.body.style.overflow = '';
        }
    });

    function setLanguageAndRedirect(lang) {
        localStorage.setItem('language', lang);
        const prefix = lang === 'pt-BR' ? 'br' : 'us';
        const currentPath = window.location.pathname.replace(/^\/(br|us)/, '');
        window.location.href = `/${prefix}${currentPath}${window.location.search}${window.location.hash}`;
    }

    document.querySelectorAll('[data-lang]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const lang = btn.getAttribute('data-lang');
            setLanguageAndRedirect(lang);
        });
    });

    function updateThemeIcon(theme) {
        const icon = themeBtn?.querySelector('i');
        if (icon) {
            icon.className = theme === 'light' ? 'fa-solid fa-moon' : 'fa-solid fa-sun';
        }
    }
});