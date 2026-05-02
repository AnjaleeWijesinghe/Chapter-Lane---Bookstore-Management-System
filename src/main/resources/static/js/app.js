document.addEventListener("DOMContentLoaded", () => {
    const revealObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("is-visible");
                revealObserver.unobserve(entry.target);
            }
        });
    }, {threshold: 0.15});

    document.querySelectorAll(".reveal").forEach((element) => revealObserver.observe(element));

    document.querySelectorAll("[data-auto-dismiss]").forEach((flashCard, index) => {
        setTimeout(() => {
            flashCard.style.opacity = "0";
            flashCard.style.transform = "translateY(16px)";
            setTimeout(() => flashCard.remove(), 300);
        }, 4200 + (index * 250));
    });

    const header = document.querySelector(".site-header");
    if (header) {
        const applyHeaderState = () => {
            header.classList.toggle("is-condensed", window.scrollY > 24);
        };
        applyHeaderState();
        window.addEventListener("scroll", applyHeaderState, {passive: true});
    }

    const bookTypeSelect = document.querySelector('select[name="bookType"]');
    if (bookTypeSelect) {
        const fieldMap = {
            printed: [
                document.querySelector('input[name="pageCount"]')?.closest("label"),
                document.querySelector('input[name="shelfCode"]')?.closest("label")
            ],
            digital: [
                document.querySelector('input[name="fileFormat"]')?.closest("label"),
                document.querySelector('input[name="fileSizeMb"]')?.closest("label")
            ]
        };

        const toggleBookFields = () => {
            const isPrinted = bookTypeSelect.value === "PRINTED";
            fieldMap.printed.forEach((field) => {
                if (field) {
                    field.style.display = isPrinted ? "grid" : "none";
                }
            });
            fieldMap.digital.forEach((field) => {
                if (field) {
                    field.style.display = isPrinted ? "none" : "grid";
                }
            });
        };

        toggleBookFields();
        bookTypeSelect.addEventListener("change", toggleBookFields);
    }
});
