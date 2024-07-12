import { ServerMockNotConnectedUtils } from "../utils/servermock-not-connected.utils"

describe('Page d\'accueil sans utilisateur connecté', () => {
  beforeEach(() => {
    ServerMockNotConnectedUtils.homepageAdsMock()
  })

  it('Visits the home page, checks that logo, header and description are visible', () => {
    cy.visit('/')
    cy.get('[data-cy=header]').should('be.visible')
    cy.get('[data-cy=logo]').should('be.visible')
    cy.get('[data-cy=global-description]').should('be.visible')
    cy.get('[data-cy=nav]').should('be.visible').should('contain', 'ACCUEIL')
    cy.get('[data-cy=nav]').should('be.visible').should('contain', 'RECHERCHER')
    cy.get('[data-cy=connexion-inscription]').should('be.visible')
    cy.get('[data-cy=connected-nav]').should('not.exist')
  })

  it('Check that the nav is set to not connected state', () => {
    cy.get('[data-cy=connexion-inscription]').should('be.visible')
  })

  it('Checks that ads are visible', () => {
    cy.get('[data-cy=ads]').should('be.visible')
    cy.get('[data-cy=ads]').children().should('have.length', 3)
  })

  it('Checks that the footer is visible', () => {
    cy.get('[data-cy=footer]').should('be.visible')
    cy.get('[data-cy=footer]').should('contain', '© 2023 Let\'s Play. Tous droits réservés.')
    cy.get('[data-cy=footer]').should('contain', 'A propos')
    cy.get('[data-cy=footer]').should('contain', 'CGU')
    cy.get('[data-cy=footer]').should('contain', 'Contact')
  })

  it('Checks that the footer links are working', () => {
    cy.get('[data-cy=footer]').contains('A propos').click()
    cy.url().should('include', '/about')

    cy.get('[data-cy=footer]').contains('CGU').click()
    cy.url().should('include', '/cgu')

    cy.get('[data-cy=footer]').contains('Contact')
    .should('have.attr', 'href')
    .should('match', /mailto:.*/)
  })

})
