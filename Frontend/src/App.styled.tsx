import { Card, CardFooter, CardPreview, Input, Text } from "@fluentui/react-components";
import styled from "styled-components";

export const AuthCardHeader = styled(Text)`
    padding-bottom: 8px;
`;

export const AuthCard = styled(Card)`
    padding: 20px 50px;
    align-items: center;
    height: fit-content;
    min-height: 300px !important;
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
