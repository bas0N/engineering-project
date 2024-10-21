import { SignUpPanel } from "..";
import { render } from '@testing-library/react';
import { axe, toHaveNoViolations } from 'jest-axe';
import { AuthProvider } from "../../../contexts/authContext";

expect.extend(toHaveNoViolations);

describe('Sign up panel' , () => {

    it('causes no a11y violations', async () => {
        const {container} = render(<AuthProvider>
            <SignUpPanel />
        </AuthProvider>);
        const results = await axe(container);
        expect(results).toHaveNoViolations();
    });

});