import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Contacter l'auteur d'une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });

});