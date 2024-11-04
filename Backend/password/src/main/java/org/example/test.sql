SELECT i.email,
       COUNT(*)                                                               AS investment_count,
       ROUND(SUM(cf.expected_flow / POWER(1 + 0.05, cf.cash_flow_period)), 2) AS total_present_value,
       ROUND(AVG(cf.expected_flow / POWER(1 + 0.05, cf.cash_flow_period)), 2) AS avg_present_value
FROM cash_flows cf
         JOIN investors i ON cf.investor_id = i.id
GROUP BY i.email
HAVING SUM(cf.expected_flow / POWER(1 + 0.05, cf.cash_flow_period)) > 1000000
ORDER BY i.email;
