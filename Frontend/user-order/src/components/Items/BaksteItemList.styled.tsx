import { makeStyles, shorthands } from "@fluentui/react-components";

export const useBasketItemsListStyles = makeStyles({
    container: {
        ...shorthands.padding('16px'),
        ...shorthands.borderRadius('8px'),
        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
        marginBottom: '20px',
        display: 'flex',
        flexDirection: 'column',
        rowGap: '12px',
    },
    header: {
        fontWeight: 600,
        marginBottom: '8px'
    },
    item: {
        display: 'flex',
        columnGap: '12px',
        alignItems: 'center',
    },
    image: {
        width: '60px',
        height: '60px',
        objectFit: 'cover',
        borderRadius: '4px',
    },
    details: {
        display: 'flex',
        flexDirection: 'column',
        rowGap: '4px'
    },
});
