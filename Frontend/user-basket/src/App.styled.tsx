import { Text } from "@fluentui/react-components";
import styled from "styled-components";

export const BasketWrapper = styled.section`
    width: calc(100% - 10px);
    padding: 10px 5px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const BasketHeader = styled(Text).attrs({as: 'h1', size: 900, align: 'center'})`
    padding: 10px 0px 20px;
`;

export const BasketContainer = styled.div`
    width: calc(100% - 10px);
    padding: 5px;
    display: flex;
    flex-direction: column;
    gap: 40px;
    align-items: center;

    @media screen and (min-width: 1024px){
        width: calc(100% - 20px);
        flex-direction: row;
        gap: 10px;
    }
`;