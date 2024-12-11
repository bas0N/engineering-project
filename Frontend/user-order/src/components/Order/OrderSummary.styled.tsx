import { makeStyles, shorthands } from "@fluentui/react-components";

export const useOrderSummaryStyles = makeStyles({
    container: {
        ...shorthands.padding('16px'),
        ...shorthands.borderRadius('8px'),
        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
        marginTop: '20px',
        marginBottom: '20px',
        display: 'flex',
        flexDirection: 'column',
        rowGap: '4px',
    },
    label: {
        fontWeight: 600,
    }
});
