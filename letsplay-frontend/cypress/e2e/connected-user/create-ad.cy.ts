import { ServerMockConnectedUserUtils } from '../utils/servermock-connected-user.utils';

describe("Créer une annonce", () => {
  beforeEach(() => {
    ServerMockConnectedUserUtils.homepageAdsMock();
    cy.loginAsUser('Test User', 'password');

    cy.get('[data-cy=nav-dropdown]').click();
    cy.get('p-menu').find('div').contains('Nouvelle annonce').click();
  });

  it('Visits the create ad page, checks that form cannot be submitted if empty', () => {
    cy.get('h1').should('contain', 'Crée ton annonce');
    cy.get('button').contains('Poster mon annonce').should('be.disabled');
  });

  it("Check that all search modules are visible and that form can't be submitted if empty", () => {
    cy.get('[data-cy=create-form]').should('be.visible');
    cy.get('[data-cy=musician-type-from]').should('be.visible');
    cy.get('[data-cy=musician-type-from]')
      .find('span')
      .should('contain', 'Ton instrument');
    cy.get('[data-cy=music-styles]').should('be.visible');
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .should('contain', 'Sélectionne les styles de ton choix');
    cy.get('[data-cy=location]').should('be.visible');
    cy.get('[data-cy=location]')
      .find('span')
      .should('contain', 'Choisis une région');
    cy.get('button[type=submit]').should('be.visible').should('be.disabled');
  });

  it('Checks that when a band is searching for a musician, the select musician dropdown shows', () => {
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();
    cy.get('[data-cy=musician-type-searching]').should('be.visible');
  });

  it('Checks that default image is displayed when user interacts with dropdowns', () => {
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();
    cy.get('[data-cy="selected-image"]').should('have.attr', 'src').should('include', 'band');

    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un vocaliste qui recherche un groupe')
      .click();
      cy.get('[data-cy="selected-image"]').should('have.attr', 'src').should('include', 'vocalist');

      cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un batteur qui recherche un groupe')
      .click();
      cy.get('[data-cy="selected-image"]').should('have.attr', 'src').should('include', 'drums');
  });

  it('Checks that user can open the upload image widget', () => {
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();
    cy.get('[data-cy=upload-image]').click();
    cy.get('iframe').should('be.visible');
  });

  it('Fills in the form and submits it', () => {
    cy.get('[data-cy="title"]').type('Groupe recherche batteur');
    cy.get('[data-cy=musician-type-from]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();

    cy.get('.p-dropdown-items').find('li')
      .contains('Un groupe qui recherche un musicien')
      .click();

    cy.get('[data-cy=musician-type-searching]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();
    cy.get('.p-dropdown-items').find('li').contains('Un batteur').click();

    cy.get('[data-cy=music-styles]').find('p-multiselect').find('chevrondownicon').click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li')
      .first()
      .click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li').eq(1)
      .click();
    cy.get('[data-cy=music-styles]')
      .find('p-multiselect')
      .find('li').eq(2)
      .click();
    cy.get('[data-cy=music-styles]').find('p-multiselect').find('chevrondownicon').click();

    cy.get('[data-cy=location]')
      .find('p-dropdown')
      .find('chevrondownicon')
      .click();

    cy.get('[data-cy=description').type('Nous sommes un groupe de death qui recherche un batteur pour compléter notre formation');

    cy.get('.p-dropdown-items')
      .find('li')
      .contains('Centre-Val de Loire')
      .click();

    cy.get('button[type=submit]').should('be.enabled');

    cy.get('button[type=submit]').click();
    cy.wait(1000);
    cy.get('p-toast')
      .should('be.visible')
      .should('contain', 'Annonce créée avec succès');

    cy.url().should('include', '/home');
  });

});