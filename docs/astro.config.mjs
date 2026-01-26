// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import markdoc from '@astrojs/markdoc';
import starlightLinksValidator from 'starlight-links-validator'
import { remarkHeadingId } from "remark-custom-heading-id";

// https://astro.build/config
const isPreview = process.env.ASTRO_PREVIEW === '1';
const site = process.env.ASTRO_SITE ?? 'https://kaumei-jdbc.kaumei.io';
const base = process.env.ASTRO_BASE ?? '/';

export default defineConfig({
	vite: {
		define: {
			__BUILD_DATE__: JSON.stringify(new Date().toISOString()),
		},
	},
	site,
	base,
	integrations: [
		starlight({
			plugins: [
				starlightLinksValidator({
					exclude: [
						'/root/README.md',
					],
				}),
			],
			title: '',
			customCss: isPreview ? ['./src/styles/preview.css'] : [],

			logo: {
				light: './src/assets/Logo_Kaumei_JDBC-light.svg',
				dark: './src/assets/Logo_Kaumei_JDBC-dark.svg',
				alt: "Kaumei JDBC",
			},

			lastUpdated: true,
			social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/kaumei/kaumei-jdbc' }],
			credits: true,
			sidebar: [
				{
					label: 'Introduction',
					collapsed: true,
					items: [
						'intro/introduction',
						'intro/why-kaumei',
						'intro/installation',
						'intro/example'
					],
				},
				{
					label: 'Integration',
					collapsed: true,
					items: [
						'integration/overview',
						'integration/datasource',
						'integration/hibernate',
						'integration/jpa',
					]
				},
				{
					label: 'Specification',
					collapsed: true,
					items: [
						'spec/spec',
						'spec/param-binding',
						'spec/result-mapping',
						'spec/converter-lookup',
						'spec/jdbc-select',
						'spec/jdbc-update',
						'spec/jdbc-native',
						'spec/others',
						'spec/configuration',
					]
				},
				{
					label: 'Roadmap',
					collapsed: true,
					items: [
						'roadmap/release-0-1-0',
						'roadmap/roadmap',
					]
				},
			],
		}),
		markdoc(),
	],
	markdown: {
		remarkPlugins: [remarkHeadingId],
	},
});
