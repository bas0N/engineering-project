import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent } from '@testing-library/react';
import { Features } from "../Features";

expect.extend(toHaveNoViolations)

const MOCK_SET_FEATURES = jest.fn();
const MOCK_FEATURES:string[] = ['super','produkt']

describe('Features', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<Features 
            features={MOCK_FEATURES}
            setFeatures={MOCK_SET_FEATURES}
        />
        );
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to add new feature', () => {
        const {getByPlaceholderText} = render(<Features 
            features={MOCK_FEATURES}
            setFeatures={MOCK_SET_FEATURES}
        />
        );

        const input = getByPlaceholderText('addProduct.features.newFeaturePlaceholder') as HTMLInputElement;

        expect(input);
        fireEvent.change(input, {target: {value: 'newFeature'}});
        fireEvent.keyDown(input, {key: 'Enter', code: 'Enter'});
        expect(MOCK_SET_FEATURES).toHaveBeenCalled();
    });

    it('Should be able to move the feature up', () => {

        const {queryAllByRole} = render(<Features 
            features={MOCK_FEATURES}
            setFeatures={MOCK_SET_FEATURES}
        />
        );

        expect(queryAllByRole('button', {name: 'addProduct.features.moveUpLabel'}).length).toEqual(2);
        const secondButton = queryAllByRole('button', {name: 'addProduct.features.moveUpLabel'})[1] as HTMLButtonElement;
        fireEvent.click(secondButton);
        expect(MOCK_SET_FEATURES).toHaveBeenCalledWith(['produkt','super'])
    })


    it('Should be able to move the feature down', () => {
        const {queryAllByRole} = render(<Features 
            features={MOCK_FEATURES}
            setFeatures={MOCK_SET_FEATURES}
        />
        );

        expect(queryAllByRole('button', {name: 'addProduct.features.moveDownLabel'}).length).toEqual(2);
        const secondButton = queryAllByRole('button', {name: 'addProduct.features.moveDownLabel'})[0] as HTMLButtonElement;
        fireEvent.click(secondButton);
        expect(MOCK_SET_FEATURES).toHaveBeenCalledWith(['produkt','super'])
    });

    it('Should be able to delete a feature', () => {
        const {queryAllByRole} = render(<Features 
            features={MOCK_FEATURES}
            setFeatures={MOCK_SET_FEATURES}
        />
        );

        expect(queryAllByRole('button', {name: 'addProduct.features.deleteLabel'}).length).toEqual(2);
        const secondButton = queryAllByRole('button', {name: 'addProduct.features.deleteLabel'})[1] as HTMLButtonElement;
        fireEvent.click(secondButton);
        expect(MOCK_SET_FEATURES).toHaveBeenCalledWith(['super'])
    })
})