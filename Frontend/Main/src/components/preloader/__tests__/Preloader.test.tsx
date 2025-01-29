import { axe, toHaveNoViolations } from "jest-axe";
import { render } from "@testing-library/react";
import { Preloader } from '../Preloader';

expect.extend(toHaveNoViolations);

describe('Preloader', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<Preloader />);
        expect(await axe(container)).toHaveNoViolations();
    })
})