import { axe, toHaveNoViolations } from "jest-axe";
import { render, screen } from '@testing-library/react';
import { DetailsAndFeatures } from "../DetailsAndFeatures";

expect.extend(toHaveNoViolations);

describe('Details and Features component', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<DetailsAndFeatures 
            details={{
                lorem: "ipsum",
            }} 
            features={['mock feature1', 'mock feature2']} 
        />);
        expect(await axe(container)).toHaveNoViolations();
        expect(screen.getByText('product.detailsHeader'));
        expect(screen.getByText('lorem'));
        expect(screen.getByText('ipsum'));
        expect(screen.getByText('product.featuresHeader'));
        expect(screen.queryAllByText(/mock feature/i)).toHaveLength(2);
    });
})