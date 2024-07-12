import { ServerMockAdminUtils } from '../utils/servermock-connected-admin.utils';

describe('Interface administrateur', () => {
  beforeEach(() => {
    ServerMockAdminUtils.mockApiCalls();
    cy.loginAsAdmin('admin@mail.com', 'adminPassword');
  });

  it('Checks that header is set to admin mode', () => {
    cy.get('header').get('[data-cy=admin-nav]').should('be.visible');
    cy.get('header').get('[data-cy=connected-nav]').should('not.exist');
    cy.get('[data-cy=admin-nav]')
      .should('be.visible')
      .should('contain', 'MEMBRES');
    cy.get('[data-cy=admin-nav]')
      .should('be.visible')
      .should('contain', 'ANNONCES');
    cy.get('[data-cy=admin-nav]')
      .should('be.visible')
      .should('contain', 'DECONNEXION');
  });

  it('Checks that admin can navigate from one page to another', () => {
    cy.get('[data-cy=admin-nav]').contains('MEMBRES').click();
    cy.url().should('contain', '/admin/users');

    cy.get('[data-cy=admin-nav]').contains('ANNONCES').click();
    cy.url().should('contain', '/admin/ads');
  });

  it('Checks that admin can logout', () => {
    cy.get('[data-cy=admin-nav]').contains('DECONNEXION').click();
    cy.url().should('contain', '/home');
    cy.get('header').get('[data-cy=admin-nav]').should('not.exist');
    cy.get('[data-cy=connexion-inscription]').should('be.visible');
  });
});
