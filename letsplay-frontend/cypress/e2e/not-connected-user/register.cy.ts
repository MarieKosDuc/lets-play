import { ServerMockNotConnectedUtils } from "../utils/servermock-not-connected.utils"

describe('Page de création de compte', () => {
    beforeEach(() => {
        ServerMockNotConnectedUtils.homepageAdsMock();
  
      cy.visit('/login');
      cy.get('[data-cy=register]').click();
    })

    it('Visits the register page', () => {
        cy.url().should('include', '/register');
        cy.get('[data-cy=global-description]').should('not.exist')
        cy.get('h1').should('be.visible').should('contain', 'Crée ton compte !');
        cy.get('form').should('be.visible');
        cy.get('input[type=text]').eq(0).should('be.visible').should('have.attr', 'placeholder', 'Ton nom');
        cy.get('input[type=email]').should('be.visible').should('have.attr', 'placeholder', 'e-mail');
        cy.get('input[type=password]').eq(0).should('be.visible').should('have.attr', 'placeholder', 'Mot de passe');
        cy.get('input[type=password]').eq(1).should('be.visible').should('have.attr', 'placeholder', 'Confirmer le mot de passe');
        cy.get('input[type=checkbox]').should('be.visible').should('have.attr', 'id', 'terms');
        cy.get('button[type=submit]').should('be.visible').should('be.disabled');
    })

    it("Checks that user can go back to login page", () => {
        cy.get('[data-cy=login]').click();
        cy.url().should('include', '/login');
    })

    it('Checks that the terms and conditions can be read by the user', () => {
        cy.get('[data-cy=terms]').click();
        cy.url().should('include', '/cgu');
    })

    it('Fills in the register form and check that it can be submitted', () => {
        cy.get('input[type=text]').eq(0).type('John Doe');
        cy.get('input[type=email]').type('mail@exmple.com');
        cy.get('input[type=password]').eq(0).type('password');
        cy.get('input[type=password]').eq(1).type('password');
        cy.get('input[type=checkbox]').check();
        cy.get('button[type=submit]').should('not.be.disabled');
    })
})