import { ServerMockLoginUtils } from '../utils/servermock-login.utils';

describe('Page de login', () => {
  beforeEach(() => {
    ServerMockLoginUtils.homepageAdsMock();
    cy.visit('/login');
  });

  it('Visits the login page', () => {
    cy.url().should('include', '/login');
    cy.get('h1')
      .should('be.visible')
      .should('contain', 'Connecte-toi pour accéder à ton espace personnel !');
    cy.get('form').should('be.visible');
    cy.get('input[type=email]').should('be.visible');
    cy.get('input[type=password]').should('be.visible');
    cy.get('button[type=submit]').should('be.visible').should('be.disabled');
  });

  it('Fills in the login form and check that it can be submitted', () => {
    cy.get('input[type=email]').type('mail@test.com');
    cy.get('input[type=password]').type('password');
    cy.get('button[type=submit]').should('not.be.disabled');
  });

  it('Fills in the login form, submit it, gets redirected to homepage', () => {
    cy.get('input[type=email]').type('mail@test.com');
    cy.get('input[type=password]').type('password');
    cy.get('button[type=submit]').should('not.be.disabled').click();
    cy.wait(1000);
    cy.get('p-toast')
      .should('be.visible')
      .should('contain', 'Connexion réussie');
    cy.url().should('include', '/home');
  });

  it('Gets redirected, check that the nav is set to connected state', () => {
    cy.loginAsUser('mail@test.com', 'password');
    cy.get('[data-cy=connexion-inscription]').should('not.exist');
    cy.get('[data-cy=connected-nav]').should('be.visible');
  });
});
