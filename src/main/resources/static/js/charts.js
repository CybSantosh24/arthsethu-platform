// ArthSethu Chart.js Utilities
// Comprehensive financial visualization library

// Chart.js default configuration
Chart.defaults.color = '#bdc3c7';
Chart.defaults.borderColor = '#34495e';
Chart.defaults.backgroundColor = 'rgba(52, 152, 219, 0.1)';

// Color palette for consistent theming
const ArthSethuColors = {
    primary: '#3498db',
    success: '#00d4aa',
    warning: '#f39c12',
    danger: '#e74c3c',
    info: '#3498db',
    light: '#ecf0f1',
    dark: '#2c3e50',
    muted: '#95a5a6',
    
    // Gradient colors
    gradients: {
        primary: ['#3498db', '#2980b9'],
        success: ['#00d4aa', '#00a085'],
        warning: ['#f39c12', '#e67e22'],
        danger: ['#e74c3c', '#c0392b']
    },
    
    // Chart specific colors
    capex: '#e74c3c',
    opex: '#3498db',
    revenue: '#00d4aa',
    profit: '#27ae60',
    loss: '#e74c3c',
    neutral: '#95a5a6'
};

// Utility function to create gradients
function createGradient(ctx, colors, direction = 'vertical') {
    const gradient = direction === 'vertical' 
        ? ctx.createLinearGradient(0, 0, 0, 400)
        : ctx.createLinearGradient(0, 0, 400, 0);
    
    gradient.addColorStop(0, colors[0]);
    gradient.addColorStop(1, colors[1]);
    return gradient;
}

// CAPEX vs OPEX Pie Chart
function createCapexOpexChart(canvasId, capex, opex, options = {}) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    const total = capex + opex;
    const capexPercentage = ((capex / total) * 100).toFixed(1);
    const opexPercentage = ((opex / total) * 100).toFixed(1);
    
    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: [
                `CAPEX (${capexPercentage}%)`,
                `OPEX (${opexPercentage}%)`
            ],
            datasets: [{
                data: [capex, opex],
                backgroundColor: [
                    ArthSethuColors.capex,
                    ArthSethuColors.opex
                ],
                borderColor: '#2c3e50',
                borderWidth: 2,
                hoverBorderWidth: 3,
                hoverBorderColor: '#ecf0f1'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true,
                        font: {
                            size: 14,
                            weight: '500'
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed;
                            const formatted = new Intl.NumberFormat('en-IN', {
                                style: 'currency',
                                currency: 'INR',
                                minimumFractionDigits: 0,
                                maximumFractionDigits: 0
                            }).format(value);
                            return `${label}: ${formatted}`;
                        }
                    },
                    backgroundColor: 'rgba(44, 62, 80, 0.9)',
                    titleColor: '#ecf0f1',
                    bodyColor: '#ecf0f1',
                    borderColor: '#3498db',
                    borderWidth: 1
                }
            },
            cutout: '60%',
            ...options
        }
    });
}

// Health Score Speedometer Gauge
function createHealthScoreGauge(canvasId, score, options = {}) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    // Determine color based on score
    let scoreColor = ArthSethuColors.danger; // Red for poor (0-39)
    if (score >= 80) scoreColor = ArthSethuColors.success; // Green for excellent (80-100)
    else if (score >= 60) scoreColor = ArthSethuColors.warning; // Orange for good (60-79)
    else if (score >= 40) scoreColor = '#e67e22'; // Orange-red for fair (40-59)
    
    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [score, 100 - score],
                backgroundColor: [scoreColor, '#34495e'],
                borderWidth: 0,
                cutout: '75%'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            rotation: -90,
            circumference: 180,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: false
                }
            },
            ...options
        }
    });
}

// Revenue Trend Line Chart
function createRevenueTrendChart(canvasId, data, options = {}) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    const gradient = createGradient(ctx.getContext('2d'), ArthSethuColors.gradients.success);
    
    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Revenue',
                data: data.revenue,
                borderColor: ArthSethuColors.success,
                backgroundColor: gradient,
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: ArthSethuColors.success,
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(44, 62, 80, 0.9)',
                    titleColor: '#ecf0f1',
                    bodyColor: '#ecf0f1',
                    borderColor: ArthSethuColors.success,
                    borderWidth: 1,
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed.y;
                            return `Revenue: ₹${value.toLocaleString('en-IN')}`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7'
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7',
                        callback: function(value) {
                            return '₹' + value.toLocaleString('en-IN');
                        }
                    }
                }
            },
            ...options
        }
    });
}

// Multi-metric Dashboard Chart
function createDashboardChart(canvasId, data, options = {}) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Health Score',
                data: data.healthScores,
                borderColor: ArthSethuColors.success,
                backgroundColor: 'rgba(0, 212, 170, 0.1)',
                borderWidth: 3,
                fill: false,
                tension: 0.4,
                pointBackgroundColor: ArthSethuColors.success,
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 5,
                yAxisID: 'y'
            }, {
                label: 'Margin %',
                data: data.margins,
                borderColor: ArthSethuColors.primary,
                backgroundColor: 'rgba(52, 152, 219, 0.1)',
                borderWidth: 2,
                fill: false,
                tension: 0.4,
                pointBackgroundColor: ArthSethuColors.primary,
                pointBorderColor: '#ffffff',
                pointBorderWidth: 1,
                pointRadius: 3,
                yAxisID: 'y1'
            }, {
                label: 'Wastage %',
                data: data.wastage,
                borderColor: ArthSethuColors.danger,
                backgroundColor: 'rgba(231, 76, 60, 0.1)',
                borderWidth: 2,
                fill: false,
                tension: 0.4,
                pointBackgroundColor: ArthSethuColors.danger,
                pointBorderColor: '#ffffff',
                pointBorderWidth: 1,
                pointRadius: 3,
                yAxisID: 'y1'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    labels: {
                        color: '#bdc3c7',
                        usePointStyle: true,
                        padding: 20
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(44, 62, 80, 0.9)',
                    titleColor: '#ecf0f1',
                    bodyColor: '#ecf0f1',
                    borderColor: ArthSethuColors.primary,
                    borderWidth: 1
                }
            },
            scales: {
                x: {
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7'
                    }
                },
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    beginAtZero: true,
                    max: 100,
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7'
                    },
                    title: {
                        display: true,
                        text: 'Health Score',
                        color: ArthSethuColors.success
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    beginAtZero: true,
                    grid: {
                        drawOnChartArea: false,
                    },
                    ticks: {
                        color: '#bdc3c7'
                    },
                    title: {
                        display: true,
                        text: 'Percentage (%)',
                        color: '#bdc3c7'
                    }
                }
            },
            ...options
        }
    });
}

// Break-even Analysis Chart
function createBreakEvenChart(canvasId, data, options = {}) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.months,
            datasets: [{
                label: 'Revenue',
                data: data.revenue,
                borderColor: ArthSethuColors.success,
                backgroundColor: 'rgba(0, 212, 170, 0.1)',
                borderWidth: 3,
                fill: false,
                tension: 0.4
            }, {
                label: 'Total Costs',
                data: data.costs,
                borderColor: ArthSethuColors.danger,
                backgroundColor: 'rgba(231, 76, 60, 0.1)',
                borderWidth: 3,
                fill: false,
                tension: 0.4
            }, {
                label: 'Break-even Point',
                data: data.breakEven,
                borderColor: ArthSethuColors.warning,
                backgroundColor: 'rgba(243, 156, 18, 0.1)',
                borderWidth: 2,
                borderDash: [5, 5],
                fill: false,
                pointRadius: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    labels: {
                        color: '#bdc3c7',
                        usePointStyle: true,
                        padding: 20
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(44, 62, 80, 0.9)',
                    titleColor: '#ecf0f1',
                    bodyColor: '#ecf0f1',
                    borderColor: ArthSethuColors.primary,
                    borderWidth: 1,
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed.y;
                            return `${context.dataset.label}: ₹${value.toLocaleString('en-IN')}`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7'
                    },
                    title: {
                        display: true,
                        text: 'Months',
                        color: '#bdc3c7'
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7',
                        callback: function(value) {
                            return '₹' + value.toLocaleString('en-IN');
                        }
                    },
                    title: {
                        display: true,
                        text: 'Amount (₹)',
                        color: '#bdc3c7'
                    }
                }
            },
            ...options
        }
    });
}

// Monthly Performance Bar Chart
function createMonthlyPerformanceChart(canvasId, data, options = {}) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;
    
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.months,
            datasets: [{
                label: 'Sales',
                data: data.sales,
                backgroundColor: ArthSethuColors.success,
                borderColor: ArthSethuColors.success,
                borderWidth: 1
            }, {
                label: 'Expenses',
                data: data.expenses,
                backgroundColor: ArthSethuColors.danger,
                borderColor: ArthSethuColors.danger,
                borderWidth: 1
            }, {
                label: 'Profit',
                data: data.profit,
                backgroundColor: ArthSethuColors.primary,
                borderColor: ArthSethuColors.primary,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: {
                        color: '#bdc3c7',
                        usePointStyle: true,
                        padding: 20
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(44, 62, 80, 0.9)',
                    titleColor: '#ecf0f1',
                    bodyColor: '#ecf0f1',
                    borderColor: ArthSethuColors.primary,
                    borderWidth: 1,
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed.y;
                            return `${context.dataset.label}: ₹${value.toLocaleString('en-IN')}`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7'
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: '#34495e',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#bdc3c7',
                        callback: function(value) {
                            return '₹' + value.toLocaleString('en-IN');
                        }
                    }
                }
            },
            ...options
        }
    });
}

// Export all chart functions
window.ArthSethuCharts = {
    createCapexOpexChart,
    createHealthScoreGauge,
    createRevenueTrendChart,
    createDashboardChart,
    createBreakEvenChart,
    createMonthlyPerformanceChart,
    colors: ArthSethuColors,
    createGradient
};