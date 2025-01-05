import { Input, Text } from "@fluentui/react-components";
import styled from "styled-components";

export const AddProductWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const AddProductHeader = styled(Text).attrs({align: 'center'})`
    padding: 10px 5px 30px;
`;

export const AddProductInput = styled(Input).attrs({appearance: 'underline'})`
    width: calc(60% - 20px);
    padding: 10px;
`;