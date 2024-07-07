import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Modification d'une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });
});