#!/usr/bin/env node

const pool = require('../config/database');
const User = require('../models/User');

async function addAdmin() {
    try {
        console.log('Creating administrator account...');
        
        const username = 'admin';
        const password = process.env.ADMIN_PASSWORD || 'RCTF2025';
        const email = 'admin@rois.team';
        
        const existingUser = await User.findByUsername(username);
        if (existingUser) {
            console.log('Administrator account already exists!');
            process.exit(1);
        }
        
        const existingEmail = await User.findByEmail(email);
        if (existingEmail) {
            console.log('Email already in use!');
            process.exit(1);
        }
        
        const userId = await User.create({
            username: username,
            email: email,
            password: password,
            type: 0,
            displayName: 'Administrator',
            department: 'System',
            role: 'admin'
        });
        
        console.log('Administrator account created successfully!');
        console.log(`Password: ${password.replace(/./g, '*')}`);
        console.log('\nPlease keep the administrator password safe!');
        
        process.exit(0);
    } catch (error) {
        console.error('Failed to create administrator account:', error.message);
        console.error(error);
        process.exit(1);
    } finally {
        await pool.end();
    }
}

addAdmin();

