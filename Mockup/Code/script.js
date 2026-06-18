const ROLE_CONFIG = {
  "super-admin": {
    label: "Super admin",
    userName: "Admin Aurora",
    faq: "#faq-super-admin",
    navigation: [
      ["Dashboard", "#dashboard-super-admin"],
      ["FAQ", "#faq-super-admin"],
    ],
  },
  doctor: {
    label: "Veterinario",
    userName: "Dott. Camillo Zampetti",
    faq: "#faq-doctor",
    navigation: [
      ["Dashboard", "#dashboard-doctor"],
      ["Gestisci animali", "#view-animal-doctor"],
      ["FAQ", "#faq-doctor"],
    ],
  },
  receptionist: {
    label: "Reception",
    userName: "Giulia Ferri",
    faq: "#faq-receptionist",
    navigation: [
      ["Dashboard", "#dashboard-receptionist"],
      ["Gestisci appuntamenti", "#view-appointment-receptionist"],
      ["Gestisci clienti", "#view-clients-receptionist"],
      ["Gestisci pagamenti", "#view-payment-receptionist"],
      ["FAQ", "#faq-receptionist"],
    ],
  },
  client: {
    label: "Cliente",
    userName: "Andrea Rossi",
    faq: "#faq-client",
    navigation: [
      ["Dashboard", "#dashboard-client"],
      ["Prenota appuntamento", "#booking-client"],
      ["Gestisci pagamenti", "#view-payment-client"],
      ["FAQ", "#faq-client"],
    ],
  },
};

const helpAction = document.querySelector("#help-action");

function getScreenRole(screen) {
  return Object.keys(ROLE_CONFIG).find((role) =>
    screen?.classList.contains(`role-${role}`),
  );
}

function createUserBlock(config) {
  const wrapper = document.createElement("div");
  const role = document.createElement("span");
  const userName = document.createElement("strong");

  wrapper.className = "nav-user";
  role.textContent = config.label;
  userName.textContent = config.userName;
  wrapper.append(role, userName);

  return wrapper;
}

function renderNavigation() {
  document.querySelectorAll(".screen .navbar").forEach((navbar) => {
    const role = getScreenRole(navbar.closest(".screen"));
    const config = ROLE_CONFIG[role];

    if (!config) return;

    const links = config.navigation.map(([label, href]) => {
      const link = document.createElement("a");
      link.href = href;
      link.textContent = label;
      return link;
    });

    navbar.replaceChildren(createUserBlock(config), ...links);
  });
}

function updateHelpAction() {
  const screen = document.getElementById(window.location.hash.slice(1));
  const role = getScreenRole(screen);
  const config = ROLE_CONFIG[role];

  helpAction.classList.remove("is-visible");

  if (!config) return;

  const isFaq = window.location.hash === config.faq;

  if (role === "client" && isFaq) return;

  helpAction.href = config.faq;
  helpAction.textContent = isFaq ? "APRI TICKET ASSISTENZA" : "AIUTO";
  helpAction.classList.add("is-visible");
}

renderNavigation();
updateHelpAction();
window.addEventListener("hashchange", updateHelpAction);
