// ==========================================
            // Usuário logado: menu leva ao perfil
            // ==========================================
            const hamburger = document.getElementById("hamburger-menu");

            hamburger.addEventListener("click", () => {
                window.location.href = "profile.html";
            });


            // ==========================================
            // Lógica do Vídeo, Modal Inicial e Mute
            // ==========================================
            document.addEventListener("DOMContentLoaded", () => {
                const video = document.getElementById("hero-video");
                const videoModal = document.getElementById("video-modal");
                const closeVideoModalBtn = document.getElementById("close-video-modal");
                const btnMuted = document.getElementById("btn-muted");
                const btnUnmuted = document.getElementById("btn-unmuted");
                
                const muteToggleBtn = document.getElementById("mute-toggle");
                const iconMuted = document.getElementById("icon-muted");
                const iconUnmuted = document.getElementById("icon-unmuted");

                video.volume = 0.2;
                video.muted = false;
                
                let playPromise = video.play();

                if (playPromise !== undefined) {
                    playPromise.then(_ => {
                        updateMuteIcons(false);
                    })
                    .catch(error => {
                        videoModal.classList.add("active");
                        video.muted = true;
                        updateMuteIcons(true);
                        video.play().catch(e => console.log("Autoplay mutado bloqueado:", e));
                    });
                }

                function updateMuteIcons(isMuted) {
                    if(isMuted) {
                        iconMuted.style.display = "block";
                        iconUnmuted.style.display = "none";
                    } else {
                        iconMuted.style.display = "none";
                        iconUnmuted.style.display = "block";
                    }
                }

                function fecharModalVideo() {
                    videoModal.classList.remove("active");
                }

                btnMuted.addEventListener("click", () => {
                    video.muted = true;
                    updateMuteIcons(true);
                    video.play();
                    fecharModalVideo();
                });

                btnUnmuted.addEventListener("click", () => {
                    video.muted = false;
                    updateMuteIcons(false);
                    video.play();
                    fecharModalVideo();
                });

                closeVideoModalBtn.addEventListener("click", fecharModalVideo);

                videoModal.addEventListener("click", (e) => {
                    if(e.target === videoModal) {
                        fecharModalVideo();
                    }
                });

                muteToggleBtn.addEventListener("click", () => {
                    video.muted = !video.muted;
                    updateMuteIcons(video.muted);
                });
            });



