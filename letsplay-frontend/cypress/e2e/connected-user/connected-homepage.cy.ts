import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Page d'accueil utilisateur connecté", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');
  });

  it('Checks that nav is set to connected state and not to admin', () => {
    cy.get('[data-cy=connected-nav]').should('be.visible');
    cy.get('[data-cy=connexion-inscription]').should('not.exist');
    cy.get('[data-cy=admin-nav]').should('not.exist');
  });

  it('Checks that user can use the dropdown nav menu', () => {
    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').should('have.length', 5);
    cy.get('p-menu').find('div').contains('Profil').click();
    cy.url().should('include', '/profile');
    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Nouvelle annonce').click();
    cy.url().should('include', '/create');
    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Favorites').click();
    cy.url().should('include', '/fav-ads');
    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Déconnexion').click();
    cy.wait(1000);
    cy.url().should('include', '/');
  });

  it('Checks that user can see the list of ads', () => {
    cy.get('[data-cy=ads]').should('be.visible');
    cy.get('[data-cy=ads]').children().should('have.length', 3);
  });


});
