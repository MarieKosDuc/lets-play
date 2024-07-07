import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Profil d'un autre utilisateur", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });
});