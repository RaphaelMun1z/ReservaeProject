<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError("username", "password") displayInfo=false; section >
<#if section = "header">
    Music Tour - Entrar
<#elseif section = "form">
    <div class="matchpass-page">
        <header class="matchpass-header">
            <#if client?? && client.baseUrl?? && client.baseUrl?has_content>
                <a href="${client.baseUrl}" class="matchpass-logo" aria-label="Voltar ao Music Tour" >tkts</a>
            <#else>
                <span class="matchpass-logo" aria-label="Music Tour">tkts</span>
            </#if>
        </header>
        <main class="matchpass-main">
            <div class="matchpass-glow" aria-hidden="true">
            </div>
            <section class="matchpass-card">
                <div class="matchpass-introduction">
                    <h1>Bem-vindo de volta!</h1>
                    <p>Acesse sua conta para gerenciar seus ingressos.</p>
                </div>
                <#if messagesPerField.existsError("username", "password")>
                    <div class="matchpass-alert" role="alert" aria-live="polite" >${kcSanitize( messagesPerField.getFirstError( "username", "password" ) )?no_esc}</div>
                </#if>
                <form id="kc-form-login" class="matchpass-form" action="${url.loginAction}" method="post" >
                    <#if !usernameHidden??>
                        <div class="matchpass-field">
                            <label for="username">
                                <#if !realm.loginWithEmailAllowed>
                                    Usuário
                                <#elseif !realm.registrationEmailAsUsername>
                                    Usuário ou e-mail
                                <#else>
                                    E-mail
                                </#if>
                            </label>
                            <div class="matchpass-input-container">
                                <svg class="matchpass-field-icon" viewBox="0 0 24 24" aria-hidden="true" >
                                    <path d="M4 6.5h16v11H4zM4.5 7l7.5 6 7.5-6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                <input id="username" name="username" type="text" value="${(login.username!'')}" placeholder="seu@email.com" autocomplete="username" autofocus required aria-invalid="<#if messagesPerField.existsError('username', 'password')>true<#else>false</#if>" >
                                </div>
                            </div>
                        </#if>
                        <div class="matchpass-field">
                            <label for="password">Senha</label>
                            <div class="matchpass-input-container">
                                <svg class="matchpass-field-icon" viewBox="0 0 24 24" aria-hidden="true" >
                                    <rect x="5" y="10" width="14" height="10" rx="2" fill="none" stroke="currentColor" stroke-width="2" />
                                    <path d="M8 10V7a4 4 0 0 1 8 0v3" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                                </svg>
                                <input id="password" name="password" type="password" placeholder="••••••••" autocomplete="current-password" required aria-invalid="<#if messagesPerField.existsError('username', 'password')>true<#else>false</#if>" >
                                    <button class="matchpass-password-toggle" type="button" data-password-toggle aria-label="Mostrar senha" aria-controls="password" >
                                        <svg class="matchpass-eye-icon" data-eye-visible viewBox="0 0 24 24" aria-hidden="true" >
                                            <path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z" fill="none" stroke="currentColor" stroke-width="2" />
                                            <circle cx="12" cy="12" r="2.5" fill="none" stroke="currentColor" stroke-width="2" />
                                        </svg>
                                        <svg class="matchpass-eye-icon is-hidden" data-eye-hidden viewBox="0 0 24 24" aria-hidden="true" >
                                            <path d="M3 3l18 18M10.6 6.2A9.8 9.8 0 0 1 12 6c6 0 9.5 6 9.5 6a16 16 0 0 1-3.1 3.7M6.1 6.1C3.7 7.8 2.5 12 2.5 12S6 18 12 18c1.2 0 2.3-.2 3.3-.6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                                        </svg>
                                    </button>
                                </div>
                            </div>
                            <div class="matchpass-options">
                                <#if realm.rememberMe && !usernameHidden??>
                                    <label class="matchpass-remember">
                                        <input id="rememberMe" name="rememberMe" type="checkbox" <#if login.rememberMe??>
                                            checked
                                        </#if>
                                        >
                                        <span>Lembrar-me</span>
                                    </label>
                                <#else>
                                    <span>
                                    </span>
                                </#if>
                                <#if realm.resetPasswordAllowed>
                                    <a href="${url.loginResetCredentialsUrl}" class="matchpass-link matchpass-forgot" >Esqueceu a senha?</a>
                                </#if>
                            </div>
                            <button id="kc-login" name="login" type="submit" class="matchpass-submit" >Entrar na Conta</button>
                        </form>
                        <#if realm.password && realm.registrationAllowed && !registrationDisabled?? >
                            <div class="matchpass-registration">
                                <span>Ainda não tem uma conta?</span>
                                <a href="${url.registrationUrl}" class="matchpass-register-link" >Cadastre-se agora</a>
                            </div>
                        </#if>
                    </section>
                </main>
            </div>
        </#if>
        </@layout.registrationLayout>