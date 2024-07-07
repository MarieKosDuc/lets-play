import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Créer une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });
});