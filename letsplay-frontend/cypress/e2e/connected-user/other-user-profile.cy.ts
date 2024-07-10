import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Profil d'un autre utilisateur", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');

    cy.get('[data-cy=ads]').children().first();
    cy.get('[data-cy=see-profile]').first().click();
  });

  it('Checks that user can see another user profile', () => {
    cy.url().should('include', '/profile');
    cy.get('h1').should('contain', 'Eira2');
    cy.get('[data-cy=upload-image]').should('not.exist');
    cy.get('[data-cy=ads-list]').should('be.visible').should('have.length', 1);
  });

  it('Checks that user cannot modify another user\'s profile', () => {
    cy.get('[data-cy=upload-image]').should('not.exist');
    cy.get('[data-cy=change-password]').should('not.exist');
    cy.get('[data-cy=delete-account]').should('not.exist');
  });
});