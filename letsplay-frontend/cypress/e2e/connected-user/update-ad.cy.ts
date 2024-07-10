import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Modification d'une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');

    cy.get('[data-cy=ads]').children().eq(2).find('[data-cy=plus]').click();

    cy.get('[data-cy=update-ad]').click();
  });

  it('Visits the update ad page', () => {
    cy.url().should('include', '/update');
  });


});