import { Input } from "@fluentui/react-components";
import { styled } from 'styled-components';

export const CategoriesWrapper = styled.div`
    width: calc(100% - 10px);
    padding: 10px 5px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const CategoriesInput = styled(Input).attrs({appearance: 'underline'})`
    width: calc(80% - 20px);
    padding: 10px;

    @media screen and (min-width: 768px) {
        width: calc(60% - 20px);
    }
`;

export const CategoriesTagsWrapper = styled.div`
    width: calc(80% - 20px);
    padding: 10px;
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: center;

    @media screen and (min-width: 768px) {
        width: calc(50% - 20px);
    }
`;