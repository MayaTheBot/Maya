(function () {
    const savedTheme = localStorage.getItem("theme")
    const theme = savedTheme === "dark" || savedTheme === "light"
        ? savedTheme
        : "light"

    document.documentElement.setAttribute("data-theme", theme)
})()