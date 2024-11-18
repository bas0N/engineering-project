import { Card, CardFooter, CardPreview, Input, Text } from "@fluentui/react-components";
import styled from "styled-components";

export const AuthCardHeader = styled(Text)`
    padding-bottom: 8px;
`;

export const AuthCard = styled(Card)`
    width: 40vw;
    padding: 20px 50px;
    align-items: center;
    height: fit-content;
    min-height: 300px !important;
    display: flex;
    position: relative;
    left: 30vw;
    top: 20vh;
`;

export const AuthCardPreview = styled(CardPreview)`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 8px 0px 24px;
`;

export const AuthInput = styled(Input)`
    padding: 4px 0px;
`;

export const AuthCardFooter = styled(CardFooter)`
    justify-content: center;
    align-items: center;
    display: flex;
    flex-direction: column;
`;

export const AuthCardFailure = styled(Text).attrs({size: 400, align: 'center'})`
    color: red;
    padding: 10px 0px;
`;
