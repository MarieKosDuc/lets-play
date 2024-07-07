import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Profil de l'utilisateur connecté", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });
});