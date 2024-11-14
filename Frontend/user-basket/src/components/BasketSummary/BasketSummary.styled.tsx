import { Button, Divider, tokens } from "@fluentui/react-components";
import styled from "styled-components";

export const BasketSummaryWrapper = styled.div`
    width: calc(100% - 20px);
    height: fit-content;
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
    justify-content: space-between;

    @media screen and (min-width: 1024px){
        height: calc(80vh - 20px);
        width: 280px;
    }
`;

export const BasketSummaryItem = styled.div`
    width: calc(100% - 20px);
    display: flex;
    justify-content: space-between;
    padding: 20px 10px;
`;

export const BasketSummaryDivider = styled(Divider)`
    flex: 0;
    padding: 10px 0px;
`;

export const BasketSummaryBtn = styled(Button).attrs({appearance: 'subtle'})`
    width: calc(100% - 30px);
    padding: 15px;
    font-size: 24px;
    background: ${tokens.colorPaletteRoyalBlueBackground2};
    transition: all 0.4s;

    &:hover{
        filter: brightness(120%);
    }
`;