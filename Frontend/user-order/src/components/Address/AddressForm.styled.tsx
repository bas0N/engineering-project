import { makeStyles, shorthands } from "@fluentui/react-components";

export const useAddressFormStyles = makeStyles({
    container: {
        ...shorthands.padding('16px'),
        ...shorthands.borderRadius('8px'),
        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
        display: 'flex',
        flexDirection: 'column',
        rowGap: '8px',
        marginBottom: '20px',
    },
});
